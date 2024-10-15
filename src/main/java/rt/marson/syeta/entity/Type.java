package rt.marson.syeta.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Short id;

    @Column(name = "name")
    String name;

    @ManyToMany(mappedBy = "types", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    List<Tag> tags;

    @OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    List<Event> events;
}
