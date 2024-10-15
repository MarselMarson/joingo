package rt.marson.syeta.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "country")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name")
    String name;

    @Column(name = "russian_name")
    String rusName;

    @Column(name = "flag")
    String flag;

    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    List<User> residents;
}
