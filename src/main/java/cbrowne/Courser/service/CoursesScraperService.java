package cbrowne.Courser.service;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class CoursesScraperService {

    private static final String BASE_URL = "https://catalog.gmu.edu";
    private static final String COURSE_LIST_URL = BASE_URL + "/courses/";

    public List<Map<String, String>> scrapeAllCourses() {
        List<Map<String, String>> allCourses = new ArrayList<>();

        try {
            // Fetch the main course list page
            Document mainPage = Jsoup.connect(COURSE_LIST_URL).get();

            // Use a Set to store unique category URLs
            Set<String> categoryUrls = new LinkedHashSet<>();

            // Extract links to all course categories
            Elements categoryLinks = mainPage.select("ul li a[href^='/courses/']");
            for (Element link : categoryLinks) {
                String categoryUrl = BASE_URL + link.attr("href");
                categoryUrls.add(categoryUrl); // Add unique category URL
            }

            // Iterate over unique category URLs and scrape courses
            for (String categoryUrl : categoryUrls) {
                System.out.println("Scraping category: " + categoryUrl); // Debug
                allCourses.addAll(scrapeCoursesFromCategory(categoryUrl));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return allCourses;
    }

    private List<Map<String, String>> scrapeCoursesFromCategory(String categoryUrl) {
        List<Map<String, String>> courses = new ArrayList<>();

        try {
            // Fetch the category page content
            Document document = Jsoup.connect(categoryUrl).get();

            // Select course blocks
            Elements courseBlocks = document.select("div.courseblock");

            for (Element courseBlock : courseBlocks) {
                Map<String, String> courseData = new HashMap<>();

                // Extract title (course code + name)
                Element courseCode = courseBlock.selectFirst("div.courseblocktitle strong");
                Element courseName = courseBlock.selectFirst("div.courseblocktitle em");
                if (courseCode != null) {

                    String title = courseCode.text();

                    if (courseName != null) {
                        title += " " + courseName.text();
                    }

                    String[] parts = title.split(" ");
                    String courseSubject = parts[0];
                    String courseNum = parts[1].replace(":", "");


                    courseData.put("Title", title);
                    courseData.put("Subject", courseSubject);
                    courseData.put("Course Number", courseNum);
                    courseData.put("Course Name", courseSubject + courseNum);
                }

                // Extract description
                Element descriptionElement = courseBlock.selectFirst("div.courseblockdesc");
                if (descriptionElement != null) {
                    courseData.put("Description", descriptionElement.text());
                }

                // Extract additional information (e.g., schedule type, grading)
                Elements extraElements = courseBlock.select("div.courseblockextra, div.courseblockattr");
                for (Element extraElement : extraElements) {
                    String key = extraElement.selectFirst("strong") != null
                            ? extraElement.selectFirst("strong").text().replace(":", "")
                            : "Additional Info";
                    String value = extraElement.text();
                    courseData.put(key, value);
                }

                courses.add(courseData);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return courses;
    }
}