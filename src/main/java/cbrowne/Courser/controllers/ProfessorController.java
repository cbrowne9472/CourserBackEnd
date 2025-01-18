package cbrowne.Courser.controllers;

import cbrowne.Courser.dto.CommentDTO;
import cbrowne.Courser.dto.ProfessorWithCommentsDTO;
import cbrowne.Courser.models.Comment;
import cbrowne.Courser.models.Course;
import cbrowne.Courser.models.Professor;
import cbrowne.Courser.repository.CommentRepository;
import cbrowne.Courser.repository.CourseRepository;
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

    private final CourseRepository courseRepository;
    private final CommentRepository commentRepository;

    private final ProfDetailsJSONProcessor profDetailsJSONProcessor;

    public ProfessorController(CommentService commentService, ProfessorRepository professorRepository, CourseRepository courseRepository, CommentRepository commentRepository, ProfDetailsJSONProcessor profDetailsJSONProcessor) {
        this.commentService = commentService;
        this.professorRepository = professorRepository;
        this.courseRepository = courseRepository;
        this.commentRepository = commentRepository;
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
                        comment.getGrade(),
                        comment.getComment()
                ))
                .toList();

        return new ProfessorWithCommentsDTO(
                professor.getName(),
                professor.getLink(),
                comments
        );
    }

    @GetMapping("/comments")
    public ProfessorWithCommentsDTO getCommentsByCourseAndProfessor(
            @RequestParam Long courseId,
            @RequestParam Long professorId
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor not found"));

        // Fetch comments for the given course and professor
        List<CommentDTO> comments = commentRepository.findByCourseAndProfessor(course, professor)
                .stream()
                .map(comment -> new CommentDTO(
                        comment.getCourse().getCourseName(), // Assuming Course has getName()
                        comment.getQuality(),
                        comment.getDifficulty(),
                        comment.getDate(),
                        comment.getGrade(),
                        comment.getComment()
                ))
                .toList();

        // Return the DTO containing the professor's name and comments
        return new ProfessorWithCommentsDTO(professor.getName(), null, comments);
    }





}
