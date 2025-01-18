package cbrowne.Courser.service;

import cbrowne.Courser.models.Comment;
import cbrowne.Courser.models.Course;
import cbrowne.Courser.models.Professor;
import cbrowne.Courser.repository.CommentRepository;
import cbrowne.Courser.repository.CourseRepository;
import cbrowne.Courser.repository.ProfessorRepository;
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

    private final ProfessorRepository professorRepository;
    private final CourseRepository courseRepository;
    private final CommentRepository commentRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public JSONProcessingService(ProfessorRepository professorRepository, CourseRepository courseRepository,
                                 CommentRepository commentRepository, ObjectMapper objectMapper) {
        this.professorRepository = professorRepository;
        this.courseRepository = courseRepository;
        this.commentRepository = commentRepository;
        this.objectMapper = objectMapper;
    }

    public void processAndSaveJSON(String filePath) throws IOException {
        System.out.println("Starting JSON processing...");

        File file = new File(filePath);
        List<Map<String, Object>> jsonList = objectMapper.readValue(file, List.class);

        // Preload all courses from the database
        List<Course> existingCourses = courseRepository.findAll();

        int professorCount = 0; // Counter for processed professors

        for (Map<String, Object> professorNode : jsonList) {
            Map<String, Object> professorData = (Map<String, Object>) professorNode.get("node");

            // Create and populate Professor entity
            Professor professor = new Professor();
            professor.setName((String) professorData.get("firstName") + " " + (String) professorData.get("lastName"));
            professor.setDepartment((String) professorData.get("department"));

            // Handle avgDifficulty
            Object avgDifficultyObj = professorData.get("avgDifficulty");
            if (avgDifficultyObj instanceof Double) {
                professor.setAvgDifficulty((Double) avgDifficultyObj);
            } else if (avgDifficultyObj instanceof Integer) {
                professor.setAvgDifficulty(((Integer) avgDifficultyObj).doubleValue());
            }

            // Handle avgRating
            Object avgRatingObj = professorData.get("avgRating");
            if (avgRatingObj instanceof Double) {
                professor.setAvgRating((Double) avgRatingObj);
            } else if (avgRatingObj instanceof Integer) {
                professor.setAvgRating(((Integer) avgRatingObj).doubleValue());
            }

            // Handle numRatings
            Object numRatingsObj = professorData.get("numRatings");
            if (numRatingsObj instanceof Integer) {
                professor.setNumRatings((Integer) numRatingsObj);
            } else if (numRatingsObj instanceof String) {
                professor.setNumRatings(Integer.parseInt((String) numRatingsObj));
            }

            Object profileLinkObj = professorNode.get("profileLink");
            if (profileLinkObj != null && profileLinkObj instanceof String) {
                professor.setLink((String) profileLinkObj);
            } else {
                System.out.println("Profile link missing or invalid for professor: " + professor.getName());
            }


            // Save the professor to make it persistent
            professor = professorRepository.save(professor);

            // Process associated comments/ratings
            Map<String, Object> ratings = (Map<String, Object>) professorData.get("ratings");
            int commentCount = 0; // Counter for processed comments

            if (ratings != null) {
                List<Map<String, Object>> ratingEdges = (List<Map<String, Object>>) ratings.get("edges");

                for (Map<String, Object> ratingEdge : ratingEdges) {
//                    if (commentCount >= 100) {
//                        break; // Stop processing comments after 100
//                    }

                    Map<String, Object> ratingNode = (Map<String, Object>) ratingEdge.get("node");

                    String courseName = (String) ratingNode.get("class");
                    Course matchedCourse = findCourseByName(existingCourses, courseName);

                    if (matchedCourse != null) {
                        // Establish relationship between professor and course
                        if (!professor.getCourses().contains(matchedCourse)) {
                            professor.getCourses().add(matchedCourse);
                        }
                        if (!matchedCourse.getProfessors().contains(professor)) {
                            matchedCourse.getProfessors().add(professor);
                        }

                        Comment comment = new Comment();
                        comment.setProfessor(professor);
                        comment.setCourse(matchedCourse);
                        comment.setCourseName(courseName);
                        comment.setComment((String) ratingNode.get("comment"));

                        // Handle difficulty
                        Object difficultyObj = ratingNode.get("difficultyRating");
                        if (difficultyObj instanceof Integer) {
                            comment.setDifficulty(String.valueOf(difficultyObj));
                        } else if (difficultyObj instanceof String) {
                            comment.setDifficulty((String) difficultyObj);
                        }

                        // Handle clarity/quality
                        Object qualityObj = ratingNode.get("clarityRating");
                        if (qualityObj instanceof Integer) {
                            comment.setQuality(String.valueOf(qualityObj));
                        } else if (qualityObj instanceof String) {
                            comment.setQuality((String) qualityObj);
                        }

                        comment.setGrade((String) ratingNode.get("grade"));
                        comment.setDate(extractDate((String) ratingNode.get("date")));


                        // Save the comment
                        commentRepository.save(comment);

                        commentCount++; // Increment comment counter
                    }
                }
            }

            professorCount++; // Increment the professor counter
            System.out.println("Processed professor: " + professor.getName() + " with " + commentCount + " comments. Professor: " + professorCount);
        }

        System.out.println("Finished processing JSON file. Total professors processed: " + professorCount);
    }


    private Course findCourseByName(List<Course> courses, String courseName) {
        String normalizedCourseName = normalizeCourseName(courseName);
        for (Course course : courses) {
            if (normalizeCourseName(course.getCourseName()).equals(normalizedCourseName)) {
                return course;
            }
        }
        return null; // Return null if no matching course is found
    }

    private String normalizeCourseName(String courseName) {
        return courseName != null ? courseName.trim().toUpperCase() : null;
    }
    private String extractDate(String dateTime) {
        if (dateTime != null && dateTime.contains(" ")) {
            return dateTime.split(" ")[0]; // Extracts only the date part
        }
        return dateTime; // Fallback if the format is unexpected
    }


}

