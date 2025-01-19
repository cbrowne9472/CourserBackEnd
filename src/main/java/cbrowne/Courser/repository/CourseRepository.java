package cbrowne.Courser.repository;

import cbrowne.Courser.models.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Course> searchCoursesByTitle(@Param("query") String query);


    Optional<Course> findByTitle(String name);
    List<Course> findByTitleContainingIgnoreCase(String title);



    Page<Course> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT DISTINCT c.subject FROM Course c ORDER BY c.subject ASC")
    List<String> findAllSubjects();

    Page<Course> findBySubject(String subject, Pageable pageable);

    Optional<Course> findByCourseName(String name);

    Page<Course> findByCourseNumberStartingWith(String prefix, Pageable pageable);

    Page<Course> findByCourseNumberStartingWithAndSubject(String prefix, String subject, Pageable pageable);

    Page<Course> findByCourseNumberStartingWithAndSubjectAndTitleContainingIgnoreCase(String level, String subject, String title, Pageable pageable);

    Page<Course> findByCourseNumberStartingWithAndTitleContainingIgnoreCase(String level, String title, Pageable pageable);

    Page<Course> findBySubjectAndTitleContainingIgnoreCase(String subject, String title, Pageable pageable);

}
