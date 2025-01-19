package cbrowne.Courser.controllers;

import cbrowne.Courser.dto.CourseRatingDTO;
import cbrowne.Courser.dto.ProfessorAverageGradeDTO;
import cbrowne.Courser.dto.ProfessorWithCommentsDTO;
import cbrowne.Courser.dto.ProfessorWithCoursesDTO;
import cbrowne.Courser.models.*;
import cbrowne.Courser.repository.CourseRepository;
import cbrowne.Courser.repository.ProfessorRepository;
import cbrowne.Courser.service.CourseService;
import cbrowne.Courser.webtoken.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/home")
public class CourseController {

    private final CourseRepository courseRepository;
    private final CourseService courseService;

    private final ProfessorRepository professorRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository, CourseService courseService, ProfessorRepository professorRepository) {
        this.courseRepository = courseRepository;
        this.courseService = courseService;
        this.professorRepository = professorRepository;
    }

    @GetMapping("/search")
    public Map<String, List<?>> search(@RequestParam("query") String query) {
        // Ensure the query is passed exactly as provided without trimming
        List<Course> courses = courseRepository.searchCoursesByTitle(query);
        List<Professor> professors = professorRepository.searchProfessorsByName(query);

        return Map.of("courses", courses, "professors", professors);
    }



    @GetMapping("/course/{courseId}/average-grade")
    public String getAverageGrade(@PathVariable Long courseId) {
        return courseService.getAverageGradeForCourse(courseId);
    }

    @GetMapping("/course/{courseId}/professor-grades")
    public List<ProfessorAverageGradeDTO> getAverageGradesByProfessor(@PathVariable Long courseId) {
        return courseService.getAverageGradesByProfessorForCourse(courseId);
    }

    @GetMapping("/course/{courseId}/avg_rating")
    public CourseRatingDTO getAverageRatingFromCourse(@PathVariable Long courseId) {
        return courseService.getAvgRatingFromCommentsFromCourse(courseId);
    }


    @GetMapping("/course/{courseId}/professors")
    public List<ProfessorWithCoursesDTO> getProfessorsForCourse(@PathVariable Long courseId) {
        return courseService.getProfessorsForCourse(courseId);
    }


    @GetMapping("/courses")
    public List<Course> getCourses(
            @RequestParam(value = "searchQuery", required = false, defaultValue = "") String searchQuery,
            @RequestParam(value = "subject", required = false, defaultValue = "") String subject,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "sortBy", required = false, defaultValue = "courseNumber") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
            @RequestParam(value = "level", required = false, defaultValue = "") String level) {
        Pageable pageable = PageRequest.of(
                start / limit,
                limit,
                order.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending()
        );

        if (!level.isEmpty() && !subject.isEmpty() && !searchQuery.isEmpty()) {
            return courseRepository.findByCourseNumberStartingWithAndSubjectAndTitleContainingIgnoreCase(
                    level.substring(0, 1), subject, searchQuery, pageable
            ).getContent();
        }

        if (!level.isEmpty() && !subject.isEmpty()) {
            return courseRepository.findByCourseNumberStartingWithAndSubject(
                    level.substring(0, 1), subject, pageable
            ).getContent();
        }

        if (!level.isEmpty() && !searchQuery.isEmpty()) {
            return courseRepository.findByCourseNumberStartingWithAndTitleContainingIgnoreCase(
                    level.substring(0, 1), searchQuery, pageable
            ).getContent();
        }

        if (!subject.isEmpty() && !searchQuery.isEmpty()) {
            return courseRepository.findBySubjectAndTitleContainingIgnoreCase(
                    subject, searchQuery, pageable
            ).getContent();
        }

        if (!level.isEmpty()) {
            return courseRepository.findByCourseNumberStartingWith(
                    level.substring(0, 1), pageable
            ).getContent();
        }

        if (!subject.isEmpty()) {
            return courseRepository.findBySubject(subject, pageable).getContent();
        }

        if (!searchQuery.isEmpty()) {
            return courseRepository.findByTitleContainingIgnoreCase(searchQuery, pageable).getContent();
        }

        return courseRepository.findAll(pageable).getContent();
    }



    @GetMapping("/subjects")
    public List<String> getSubjects() {
        return courseRepository.findAllSubjects();
    }

    @GetMapping("/courses/search")
    public List<Course> searchCoursesByTitle(@RequestParam("title") String title) {
        return courseRepository.findByTitleContainingIgnoreCase(title);
    }

    @GetMapping("/courses/sort")
    public List<Course> sortCourses(
            @RequestParam(value = "sortBy", required = false, defaultValue = "title") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        Pageable pageable = PageRequest.of(start / limit, limit,
                order.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());
        return courseRepository.findAll(pageable).getContent();
    }


    @PostMapping("/add")
    public void addCourse(@RequestBody Course course) {
        courseService.courseAdd(course);
    }

    @DeleteMapping("/delete/{courseId}")
    public void deleteCourse(@PathVariable("courseId") Long courseId) {
        courseService.deleteCourse(courseId);
    }

    @PutMapping("/update/{courseId}")
    public void updateCourse(
            @PathVariable("courseId") Long courseId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double rating) {
        courseService.updateCourse(courseId, name, rating);
    }

    @GetMapping("/get/{courseId}")
    public Course getCourseById(@PathVariable("courseId") Long courseId){
        return courseService.getCourseById(courseId);
    }
}
