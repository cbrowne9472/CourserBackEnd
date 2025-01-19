package cbrowne.Courser.service;

import cbrowne.Courser.dto.ProfessorRatingDTO;
import cbrowne.Courser.models.Comment;
import cbrowne.Courser.models.Professor;
import cbrowne.Courser.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProfessorService {

    private ProfessorRepository professorRepository;

    @Autowired
    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    public ProfessorRatingDTO getAvgRatingAndDifficultyForProfessor(Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor not found"));

        // Get all comments for the professor
        List<Comment> comments = professor.getComments();

        // Calculate average rating
        Map<String, Long> ratingCounts = comments.stream()
                .filter(comment -> comment.getQuality() != null && isDouble(comment.getQuality()))
                .collect(Collectors.groupingBy(comment -> String.valueOf(comment.getQuality()), Collectors.counting()));

        double averageRating = comments.stream()
                .filter(comment -> comment.getQuality() != null && isDouble(comment.getQuality()))
                .mapToDouble(comment -> Double.parseDouble(comment.getQuality()))
                .average()
                .orElse(0.0);

        // Calculate average difficulty
        Map<String, Long> difficultyCounts = comments.stream()
                .filter(comment -> comment.getDifficulty() != null && isDouble(comment.getDifficulty()))
                .collect(Collectors.groupingBy(comment -> String.valueOf(comment.getDifficulty()), Collectors.counting()));

        double averageDifficulty = comments.stream()
                .filter(comment -> comment.getDifficulty() != null && isDouble(comment.getDifficulty()))
                .mapToDouble(comment -> Double.parseDouble(comment.getDifficulty()))
                .average()
                .orElse(0.0);

        return new ProfessorRatingDTO(
                Math.round(averageRating),
                new HashMap<>(ratingCounts),
                Math.round(averageDifficulty),
                new HashMap<>(difficultyCounts)
        );
    }

    // Helper method to validate if a string is a valid double
    private boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
