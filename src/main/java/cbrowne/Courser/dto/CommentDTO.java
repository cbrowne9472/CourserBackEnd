package cbrowne.Courser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDTO {
    private String courseName;
    private String quality;
    private String difficulty;
    private String date;
    private String grade;
    private String comment;
}
