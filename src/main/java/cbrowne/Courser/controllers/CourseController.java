package cbrowne.Courser.controllers;

import cbrowne.Courser.dto.ProfessorWithCommentsDTO;
import cbrowne.Courser.models.Course;
import cbrowne.Courser.repository.CourseRepository;
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

@CrossOrigin
@RestController
@RequestMapping("/home")
public class CourseController {

    private final CourseRepository courseRepository;
    private final CourseService courseService;

    @Autowired
    public CourseController(CourseRepository courseRepository, CourseService courseService) {
        this.courseRepository = courseRepository;
        this.courseService = courseService;
    }

    @GetMapping("/courses/{courseId}/professors")
    public ResponseEntity<List<ProfessorWithCommentsDTO>> getProfessorsForCourse(@PathVariable Long courseId) {
        List<ProfessorWithCommentsDTO> professors = courseService.getProfessorsForCourse(courseId);
        return ResponseEntity.ok(professors);
    }

    @GetMapping("/courses")
    public List<Course> getCourses(
            @RequestParam(value = "searchQuery", required = false, defaultValue = "") String searchQuery,
            @RequestParam(value = "subject", required = false, defaultValue = "") String subject,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "sortBy", required = false, defaultValue = "courseNumber") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order) {
        Pageable pageable = PageRequest.of(
                start / limit,
                limit,
                order.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending()
        );

        // Search by title
        if (!searchQuery.isEmpty()) {
            return courseRepository.findByTitleContainingIgnoreCase(searchQuery, pageable).getContent();
        }

        // Filter by subject
        if (!subject.isEmpty()) {
            return courseRepository.findBySubject(subject, pageable).getContent();
        }

        // Default: return all courses
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
