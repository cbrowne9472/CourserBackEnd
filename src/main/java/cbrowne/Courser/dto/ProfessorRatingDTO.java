package cbrowne.Courser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ProfessorRatingDTO {
    private long avgRating;
    private Map<String, Long> ratings; // Count of each rating value
    private long avgDifficulty;
    private Map<String, Long> difficultyCounts; // Count of each difficulty value
}

