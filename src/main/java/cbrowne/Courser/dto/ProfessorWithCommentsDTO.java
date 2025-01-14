package cbrowne.Courser.dto;

import cbrowne.Courser.models.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class ProfessorWithCommentsDTO {
    private String professorName;
    private String professorLink;
    private List<CommentDTO> comments;
}


