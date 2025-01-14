package cbrowne.Courser.service;

import cbrowne.Courser.models.College;
import cbrowne.Courser.models.Comment;
import cbrowne.Courser.models.Course;
import cbrowne.Courser.models.Professor;
import cbrowne.Courser.repository.CollegeRepository;
import cbrowne.Courser.repository.CommentRepository;
import cbrowne.Courser.repository.CourseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class JSONProcessingService {

    private final CollegeRepository collegeRepository;
    private final ObjectMapper objectMapper;
    private final CommentRepository commentRepository;

    private final CourseRepository courseRepository;

    @Autowired
    public JSONProcessingService(CollegeRepository collegeRepository, ObjectMapper objectMapper, CommentRepository commentRepository, CourseRepository courseRepository) {
        this.collegeRepository = collegeRepository;
        this.objectMapper = objectMapper;
        this.commentRepository = commentRepository;
        this.courseRepository = courseRepository;
    }

    public void processAndSaveJSON(String filePath) throws IOException {
        // Read the JSON file as a Map
        File file = new File(filePath);
        Map<String, Object> jsonMap = objectMapper.readValue(file, Map.class);
        LevenshteinDistance distanceCalculator = new LevenshteinDistance();

        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            String collegeName = entry.getKey();
            Map<String, Object> collegeData = (Map<String, Object>) entry.getValue();

            College college = objectMapper.convertValue(collegeData, College.class);
            college.setName(collegeName);

            if (college.getProfessors() != null) {
                for (Professor professor : college.getProfessors()) {
                    professor.setCollege(college); // Associate Professor with College

                    if (professor.getComments() != null) {
                        for (Comment comment : professor.getComments()) {
                            comment.setProfessor(professor); // Associate Comment with Professor
                        }

                        professor.getComments().removeIf(comment -> {
                            String normalizedCommentCourse = normalizeCourseName(comment.getCourseName());

                            // Fetch all courses and normalize their names
                            List<Course> courses = courseRepository.findAll();

                            Course bestMatch = null;
                            int bestDistance = Integer.MAX_VALUE;

                            for (Course course : courses) {
                                // Normalize database course name
                                String normalizedDbCourseName = normalizeCourseName(course.getCourseName());
                                int distance = distanceCalculator.apply(normalizedCommentCourse, normalizedDbCourseName);

                                if (distance < bestDistance) {
                                    bestDistance = distance;
                                    bestMatch = course;
                                }
                            }

                            // Match threshold (adjust as needed)
                            if (bestDistance <= 3) { // Allow up to 3 character differences
                                comment.setCourse(bestMatch);

                                // Establish relationship between professor and course
                                if (!professor.getCourses().contains(bestMatch)) {
                                    professor.getCourses().add(bestMatch);
                                }
                                if (!bestMatch.getProfessors().contains(professor)) {
                                    bestMatch.getProfessors().add(professor);
                                }

                                return false; // Keep the comment
                            }

                            System.out.println("Skipping comment: Course not found - " + comment.getCourseName());
                            return true; // Remove the comment
                        });
                    }
                }
            }

            // Save the College entity (cascading to Professors and Comments)
            collegeRepository.save(college);
        }
    }


    private String normalizeCourseName(String courseName) {
        return courseName != null ? courseName.trim().toUpperCase() : null; // Convert to uppercase
    }


}
