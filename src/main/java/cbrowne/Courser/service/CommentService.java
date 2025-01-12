package cbrowne.Courser.service;

import cbrowne.Courser.models.Comment;
import cbrowne.Courser.models.Professor;
import cbrowne.Courser.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getCommentsByProfessor(Professor professor) {
        return commentRepository.findByProfessor(professor);
    }
}