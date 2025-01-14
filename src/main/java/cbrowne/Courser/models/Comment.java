package cbrowne.Courser.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference(value = "course-comments") // Back-reference for Course -> Comments
    private Course course;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    @JsonBackReference(value = "professor-comments") // Back-reference for Professor -> Comments
    private Professor professor;

    @JsonProperty("Course") // Keep this for JSON serialization/deserialization
    private String courseName;

    @JsonProperty("Quality") // Maps JSON key "Quality" to this field
    private String quality;

    @JsonProperty("Difficulty") // Maps JSON key "Difficulty" to this field
    private String difficulty;

    @JsonProperty("Date") // Maps JSON key "Date" to this field
    private String date;

    @JsonProperty("Attendance") // Maps JSON key "Attendance" to this field
    private String attendance;

    @JsonProperty("Grade") // Maps JSON key "Grade" to this field
    private String grade;

    @JsonProperty("Textbook")
    @Column(columnDefinition = "TEXT") // Updated to TEXT
    private String textbook;

    @JsonProperty("Online Class")
    @Column(columnDefinition = "TEXT") // Updated to TEXT
    private String onlineClass;

    @JsonProperty("Comment")
    @Column(columnDefinition = "TEXT") // Updated to TEXT
    private String comment;


}


