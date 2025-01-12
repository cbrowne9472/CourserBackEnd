package cbrowne.Courser.controllers;

import cbrowne.Courser.models.Comment;
import cbrowne.Courser.models.Professor;
import cbrowne.Courser.repository.ProfessorRepository;
import cbrowne.Courser.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/prof_api")
public class ProfessorController {
    private final CommentService commentService;
    private final ProfessorRepository professorRepository;

    @Autowired
    public ProfessorController(CommentService commentService, ProfessorRepository professorRepository) {
        this.commentService = commentService;
        this.professorRepository = professorRepository;
    }

    // Endpoint to get comments for a professor by their ID
    @GetMapping("/professor/{professorId}")
    public List<Comment> getCommentsByProfessor(@PathVariable Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor not found with id: " + professorId));
        return commentService.getCommentsByProfessor(professor);
    }

}
