package cbrowne.Courser.service;

import cbrowne.Courser.dto.ProfessorWithCommentsDTO;
import cbrowne.Courser.models.Comment;
import cbrowne.Courser.models.Course;
import cbrowne.Courser.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<ProfessorWithCommentsDTO> getProfessorsForCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return course.getProfessors().stream().map(professor -> {
            List<Comment> comments = professor.getComments().stream()
                    .filter(comment -> comment.getCourse() != null && comment.getCourse().getId() == courseId) // Ensure course is not null
                    .toList();

            double avgRating = comments.stream()
                    .mapToDouble(comment -> {
                        try {
                            return Double.parseDouble(comment.getQuality());
                        } catch (NumberFormatException e) {
                            return 0.0;
                        }
                    })
                    .average()
                    .orElse(0.0);

            return new ProfessorWithCommentsDTO(
                    professor.getName(),
                    professor.getLink(),
                    comments, // Pass full Comment objects
                    avgRating
            );
        }).toList();
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
