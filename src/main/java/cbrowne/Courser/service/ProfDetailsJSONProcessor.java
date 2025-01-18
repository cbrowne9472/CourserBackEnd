package cbrowne.Courser.service;

import cbrowne.Courser.models.Professor;
import cbrowne.Courser.repository.ProfessorRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class ProfDetailsJSONProcessor {

    private final ProfessorRepository professorRepository;

    public ProfDetailsJSONProcessor(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    public void processAndStoreProfessorData(String filePath) {
        try {
            // Load JSON file
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(filePath));

            // Iterate over each college
            for (JsonNode collegeNode : rootNode) {
                JsonNode professorsNode = collegeNode.get("professors");

                if (professorsNode != null && professorsNode.isArray()) {
                    for (JsonNode professorNode : professorsNode) {
                        // Extract professor data
                        String professorName = professorNode.get("name").asText();
                        String details = professorNode.has("details") ? professorNode.get("details").asText() : null;
                        JsonNode ratingNode = professorNode.get("rating");

                        Double avgRating = ratingNode != null && ratingNode.has("avgRating")
                                ? ratingNode.get("avgRating").asDouble()
                                : null;
                        Double avgDifficulty = ratingNode != null && ratingNode.has("avgDifficulty")
                                ? ratingNode.get("avgDifficulty").asDouble()
                                : null;
                        Integer numRatings = ratingNode != null && ratingNode.has("numRatings")
                                ? ratingNode.get("numRatings").asInt()
                                : null;
                        String department = ratingNode != null && ratingNode.has("department")
                                ? ratingNode.get("department").asText()
                                : null;

                        // Find professor in the database
                        System.out.println(professorName);
                        List<Professor> professors = professorRepository.findByName(professorName);

                        if (professors.size() > 1) {
                            System.err.println("Multiple professors found with name: " + professorName);
                            continue; // Or handle this case as needed
                        }

                        Professor professor = professors.isEmpty() ? new Professor() : professors.get(0);


                        // Update professor data
                        professor.setName(professorName);
                        professor.setAvgRating(avgRating);
                        professor.setAvgDifficulty(avgDifficulty);
                        professor.setNumRatings(numRatings);
                        professor.setDepartment(department);

                        // Save to the database
                        professorRepository.save(professor);
                    }
                }
            }

            System.out.println("Professor data processed and stored successfully.");
        } catch (Exception e) {
            System.err.println("Error processing JSON file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
