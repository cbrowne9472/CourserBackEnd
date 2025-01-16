package cbrowne.Courser;

import cbrowne.Courser.models.Course;
import cbrowne.Courser.service.CourseService;
import cbrowne.Courser.service.CoursesScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class LineRunner implements CommandLineRunner {

    private final CourseService courseService;
    private final CoursesScraperService courseScraperService;

    private static final String mathDesc = "First semester of three-semester, calculus-based introductory physics sequence, designed primarily for science and engineering majors. Mechanics. Offered by Physics & Astronomy. Limited to three attempts. Equivalent to PHYS 170.";
    private static final String physDesc = "Introduces ideas of discrete mathematics and combinatorial proof techniques including mathematical induction, sets, graphs, trees, recursion, and enumeration. Offered by Mathematics. Limited to three attempts.";
    private static final String chemDesc = "Theoretical, synthetic, industrial, and biological aspects of the chemistry of carbon compounds. Offered by Chemistry. Limited to three attempts.";
    @Autowired
    public LineRunner(CourseService courseService, CoursesScraperService courseScraperService) {
        this.courseService = courseService;
        this.courseScraperService = courseScraperService;
    }

    @Override
    public void run(String... args) throws Exception {
//        courseService.courseAdd(new Course("MATH 125: Discrete Mathematics I.", 4.5, mathDesc));
//        courseService.courseAdd(new Course("PHYS 160: University Physics I.", 4.0, physDesc));
//        courseService.courseAdd(new Course("CHEM 313: Organic Chemistry I.", 3.8, chemDesc));

//        List<Map<String, String>> courses = courseScraperService.scrapeAllCourses();
//
//        courseService.saveToDatabase(courses);
    }
}
