package cbrowne.Courser.controllers;

import cbrowne.Courser.dto.CommentDTO;
import cbrowne.Courser.dto.ProfessorWithCommentsDTO;
import cbrowne.Courser.models.Comment;
import cbrowne.Courser.models.Professor;
import cbrowne.Courser.repository.ProfessorRepository;
import cbrowne.Courser.service.CommentService;
import cbrowne.Courser.service.ProfDetailsJSONProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/prof_api")
public class ProfessorController {
    private final CommentService commentService;
    private final ProfessorRepository professorRepository;

    private final ProfDetailsJSONProcessor profDetailsJSONProcessor;

    @Autowired
    public ProfessorController(CommentService commentService, ProfessorRepository professorRepository, ProfDetailsJSONProcessor profDetailsJSONProcessor) {
        this.commentService = commentService;
        this.professorRepository = professorRepository;
        this.profDetailsJSONProcessor = profDetailsJSONProcessor;
    }

    // Endpoint to get comments for a professor by their ID
    @GetMapping("/professor/{professorId}/comments")
    public ProfessorWithCommentsDTO getCommentsByProfessor(@PathVariable Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor not found"));

        List<CommentDTO> comments = professor.getComments().stream()
                .map(comment -> new CommentDTO(
                        comment.getCourseName(),
                        comment.getQuality(),
                        comment.getDifficulty(),
                        comment.getDate(),
                        comment.getAttendance(),
                        comment.getGrade(),
                        comment.getTextbook(),
                        comment.getOnlineClass(),
                        comment.getComment()
                ))
                .toList();

        return new ProfessorWithCommentsDTO(
                professor.getName(),
                professor.getLink(),
                comments
        );
    }

    @PostMapping("/professor_details/import")
    public void loadProfessorData() {
        profDetailsJSONProcessor.processAndStoreProfessorData("src/main/resources/combined_colleges_professors.json");
    }



}
