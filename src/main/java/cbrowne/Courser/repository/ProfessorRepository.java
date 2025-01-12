package cbrowne.Courser.repository;

import cbrowne.Courser.models.Comment;
import cbrowne.Courser.models.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

}
