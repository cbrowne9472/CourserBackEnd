package cbrowne.Courser.service;

import cbrowne.Courser.models.College;
import cbrowne.Courser.models.Comment;
import cbrowne.Courser.models.Professor;
import cbrowne.Courser.repository.CollegeRepository;
import cbrowne.Courser.repository.CommentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class JSONProcessingService {

    private final CollegeRepository collegeRepository;
    private final ObjectMapper objectMapper;
    private final CommentRepository commentRepository;

    @Autowired
    public JSONProcessingService(CollegeRepository collegeRepository, ObjectMapper objectMapper, CommentRepository commentRepository) {
        this.collegeRepository = collegeRepository;
        this.objectMapper = objectMapper;
        this.commentRepository = commentRepository;
    }

    public void processAndSaveJSON(String filePath) throws IOException {
        // Read the JSON file as a Map
        File file = new File(filePath);
        Map<String, Object> jsonMap = objectMapper.readValue(file, Map.class);

        // Iterate through all colleges in the JSON
        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            String collegeName = entry.getKey();
            Map<String, Object> collegeData = (Map<String, Object>) entry.getValue();

            // Map the college data to a College object
            College college = objectMapper.convertValue(collegeData, College.class);
            college.setName(collegeName); // Set the college name

            // Process professors and comments
            if (college.getProfessors() != null) {
                for (Professor professor : college.getProfessors()) {
                    professor.setCollege(college); // Associate Professor with College

                    if (professor.getComments() != null) {
                        for (Comment comment : professor.getComments()) {
                            comment.setProfessor(professor); // Associate Comment with Professor
//                            System.out.println(comment);
                        }
                    }

                }
            }

            // Save the College (and cascade to Professors and Comments)
            collegeRepository.save(college);


        }
    }
}
