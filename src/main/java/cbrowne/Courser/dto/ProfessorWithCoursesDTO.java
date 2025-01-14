package cbrowne.Courser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProfessorWithCoursesDTO {
    private Long id;
    private String name;
    private String link;
    private List<String> courseNames; // List of course names
}
