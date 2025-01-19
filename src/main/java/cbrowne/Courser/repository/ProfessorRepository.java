package cbrowne.Courser.repository;

import cbrowne.Courser.models.Comment;
import cbrowne.Courser.models.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    @Query("SELECT p FROM Professor p WHERE p.name = :name")
    List<Professor> findByName(@Param("name") String name);

    @Query("SELECT p FROM Professor p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Professor> searchProfessorsByName(@Param("query") String query);


    List<Professor> findByNameContainingIgnoreCase(String name);

}

