package cbrowne.Courser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfessorAverageGradeDTO {
    private String professorName;
    private String averageGrade;
}
