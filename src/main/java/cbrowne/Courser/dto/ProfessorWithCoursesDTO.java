package cbrowne.Courser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProfessorWithCoursesDTO {
    private Long id;                 // Unique ID for the professor
    private String name;             // Professor's name
    private String link;             // Link to RateMyProfessors profile
//    private String details;          // Details about the professor (e.g., title, department)
    private Double avgRating;        // Average rating from RateMyProfessors
    private Double avgDifficulty;    // Average difficulty rating
//    private Integer wouldTakeAgain;  // Percentage of students who would take this professor again
    private String department;       // Department of the professor
    private List<String> courseNames; // List of course names taught by the professor
}
