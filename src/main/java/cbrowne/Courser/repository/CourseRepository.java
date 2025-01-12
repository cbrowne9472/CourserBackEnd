package cbrowne.Courser.repository;

import cbrowne.Courser.models.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByTitle(String name);
    List<Course> findByTitleContainingIgnoreCase(String title);

    Page<Course> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT DISTINCT c.subject FROM Course c ORDER BY c.subject ASC")
    List<String> findAllSubjects();

    Page<Course> findBySubject(String subject, Pageable pageable);

}
