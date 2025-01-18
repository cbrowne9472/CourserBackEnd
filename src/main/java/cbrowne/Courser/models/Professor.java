package cbrowne.Courser.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Professor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String link;

    private Double avgRating;

    private Double avgDifficulty;

    private Integer numRatings;

    private String department;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "professor")
    @JsonManagedReference(value = "professor-comments") // Managed reference for Professor -> Comments
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany(mappedBy = "professors")
    @JsonIgnore // Prevent serialization of this relationship
    private List<Course> courses = new ArrayList<>();
}
