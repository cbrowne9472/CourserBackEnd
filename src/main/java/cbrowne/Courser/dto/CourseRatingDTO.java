package cbrowne.Courser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class CourseRatingDTO {

    private long avgRating;

    private Map<String, Long> ratings;

    private long avgDifficulty; // Average difficulty

    private Map<String, Long> difficultyCounts; // Difficulty counts
}
