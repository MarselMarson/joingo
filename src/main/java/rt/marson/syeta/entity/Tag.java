package rt.marson.syeta.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "name")
    String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "type_tags",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "type_id")
    )
    @EqualsAndHashCode.Exclude
    Set<Type> types = new HashSet<>();

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    List<Event> events;
}
