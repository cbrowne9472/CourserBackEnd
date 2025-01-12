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
public class College {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String link;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "college")
    @ToString.Exclude // Prevent infinite recursion
    private List<Professor> professors;
}

