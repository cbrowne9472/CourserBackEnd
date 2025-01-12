package cbrowne.Courser.controllers;


import cbrowne.Courser.service.JSONProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/json")
public class CollegeController {

    private final JSONProcessingService jsonProcessingService;

    @Autowired
    public CollegeController(JSONProcessingService jsonProcessingService) {
        this.jsonProcessingService = jsonProcessingService;
    }

    @PostMapping("/process-json")
    public String processJsonFile() {
        String filePath = "src/main/resources/professors_reviews.json";

        try {
            jsonProcessingService.processAndSaveJSON(filePath);
            return "JSON file processed and saved successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error processing JSON file: " + e.getMessage();
        }
    }
}
