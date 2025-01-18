package cbrowne.Courser.service;

import cbrowne.Courser.dto.*;
import cbrowne.Courser.models.Comment;
import cbrowne.Courser.models.Course;
import cbrowne.Courser.models.Professor;
import cbrowne.Courser.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // Reusable private helper method for calculating average grade
    private String calculateAverageGrade(List<String> grades) {
        Map<String, Integer> gradeToRank = new LinkedHashMap<>();
        gradeToRank.put("A+", 1);
        gradeToRank.put("A", 2);
        gradeToRank.put("A-", 3);
        gradeToRank.put("B+", 4);
        gradeToRank.put("B", 5);
        gradeToRank.put("B-", 6);
        gradeToRank.put("C+", 7);
        gradeToRank.put("C", 8);
        gradeToRank.put("C-", 9);
        gradeToRank.put("D+", 10);
        gradeToRank.put("D", 11);
        gradeToRank.put("D-", 12);
        gradeToRank.put("F", 13);

        Map<Integer, String> rankToGrade = gradeToRank.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        List<Integer> validRanks = grades.stream()
                .filter(gradeToRank::containsKey) // Exclude invalid grades
                .map(gradeToRank::get)           // Map grades to ranks
                .collect(Collectors.toList());

        double averageRank = validRanks.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        return rankToGrade.get((int) Math.round(averageRank));
    }

    // Original method using the helper
    public String getAverageGradeForCourse(Long courseId) {
        List<String> grades = getGradesFromCommentsFromCourse(courseId);
        return calculateAverageGrade(grades);
    }

    // New method to get average grades per professor for a specific course
    public List<ProfessorAverageGradeDTO> getAverageGradesByProfessorForCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return course.getProfessors().stream()
                .map(professor -> {
                    List<String> grades = professor.getComments().stream()
                            .filter(comment -> comment.getCourse() != null && comment.getCourse().getId() == (courseId))
                            .map(Comment::getGrade)
                            .filter(grade -> grade != null && !grade.isEmpty())
                            .toList();
                    String averageGrade = calculateAverageGrade(grades);
                    return new ProfessorAverageGradeDTO(professor.getName(), averageGrade);
                })
                .toList();
    }


    // Method to extract grades from comments for a course
    public List<String> getGradesFromCommentsFromCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return course.getProfessors().stream()
                .flatMap(professor -> professor.getComments().stream())
                .filter(comment -> comment.getCourse().getId() == (courseId))
                .map(Comment::getGrade)
                .filter(grade -> grade != null && !grade.isEmpty()) // Exclude invalid grades
                .toList();
    }

    public CourseRatingDTO getAvgRatingFromCommentsFromCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Get all comments for the given course
        List<Comment> courseComments = course.getProfessors().stream()
                .flatMap(professor -> professor.getComments().stream())
                .filter(comment -> comment.getCourse().getId() == courseId)
                .collect(Collectors.toList());

        // Calculate rating counts and average rating
        Map<String, Long> ratingCounts = groupByAndCount(courseComments, Comment::getQuality);
        double averageRating = calculateAverage(courseComments, Comment::getQuality);

        // Calculate difficulty counts and average difficulty
        Map<String, Long> difficultyCounts = groupByAndCount(courseComments, Comment::getDifficulty);
        double averageDifficulty = calculateAverage(courseComments, Comment::getDifficulty);

        // Create and return the DTO
        return new CourseRatingDTO(
                Math.round(averageRating),
                new HashMap<>(ratingCounts),
                Math.round(averageDifficulty),
                new HashMap<>(difficultyCounts)
        );
    }

    private <T> Map<String, Long> groupByAndCount(List<Comment> comments, Function<Comment, T> mapper) {
        return comments.stream()
                .map(mapper)
                .filter(value -> value != null && !value.toString().isEmpty())
                .collect(Collectors.groupingBy(
                        Object::toString,
                        Collectors.counting()
                ));
    }

    private <T> double calculateAverage(List<Comment> comments, Function<Comment, T> mapper) {
        return comments.stream()
                .map(mapper)
                .filter(value -> value != null && !value.toString().isEmpty())
                .mapToDouble(value -> Double.parseDouble(value.toString()))
                .average()
                .orElse(0.0);
    }





    public List<ProfessorWithCoursesDTO> getProfessorsForCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return course.getProfessors().stream()
                .map(professor -> new ProfessorWithCoursesDTO(
                        professor.getId(),
                        professor.getName(),
                        professor.getLink(),
//                        professor.getDetails(),
                        professor.getAvgRating(),
                        professor.getAvgDifficulty(),
                        professor.getDepartment(),
                        professor.getCourses().stream()
                                .map(Course::getCourseName) // Map to course names
                                .toList()
                ))
                .toList();
    }

    public List<Course> getCoursesWithPagination(int start, int limit) {
        Pageable pageable = PageRequest.of(start, limit);
        return courseRepository.findAll(pageable).getContent();
    }

    public void courseAdd(Course course){
        Optional<Course> courseOptional = courseRepository.findByTitle(course.getTitle());

        if(courseOptional.isPresent()){
            throw new IllegalStateException("email taken");
            //Throws exception if email is taken
        }
        courseRepository.save(course);

        System.out.println(course);
    }

    public void deleteCourse(Long courseId) {
        boolean exists = courseRepository.existsById(courseId);
        if(!exists){
            throw new IllegalStateException("course with id " + courseId + " does not exist");
        }
        courseRepository.deleteById(courseId);
    }

    public void updateCourse(Long courseId, String name, Double rating) {
        //Getting ID from the database, if not exists throw exception

        //Getting student object from the database by ID
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalStateException(
                        "course with id " + courseId + " does not exist"
                ));

        if (name != null && name.length() > 0 && !name.equals(course.getTitle())) {
            course.setTitle(name);
        }

        if (rating != null && rating > 0 && !rating.equals(course.getRating())) {
            course.setRating(rating);
        }
    }

    public Course getCourseById(Long courseId){
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalStateException("course with id " + courseId + " does not exist"));
    }

    public void saveToDatabase(List<Map<String, String>> courses) {
        for (Map<String, String> courseData : courses) {
            Course course = new Course();
            course.setTitle(courseData.get("Title"));
            course.setCourseNumber(courseData.get("Course Number"));
            course.setCourseName(courseData.get("Course Name"));
            course.setSubject(courseData.get("Subject"));
            course.setDescription(courseData.get("Description"));
            course.setAdditionalInfo(courseData.getOrDefault("Additional Info", ""));
            courseRepository.save(course);
        }
    }

}
