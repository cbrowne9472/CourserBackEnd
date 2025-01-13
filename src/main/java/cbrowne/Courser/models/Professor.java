package cbrowne.Courser.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

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

    @ManyToOne
    @JoinColumn(name = "college_id")
    @ToString.Exclude // Prevent infinite recursion
    private College college;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "professor")
    @ToString.Exclude // Prevent infinite recursion
    private List<Comment> comments;

    @ManyToMany(mappedBy = "professors")
    private List<Course> courses;

}

