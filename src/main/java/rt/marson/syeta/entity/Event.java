package rt.marson.syeta.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "name")
    String name;
    @Column(name = "description")
    String description;
    @Column(name = "info")
    String info;

    @Column(name = "schedule")
    @JdbcTypeCode(SqlTypes.JSON)
    List<Schedule> schedule;

    @Column(name = "location_latlng")
    String locationLatLng;

    @Column(name = "location")
    Point location;

    @Transient
    Double distanceToUser;

    @Column(name = "city")
    String city;
    @Column(name = "state")
    String state;
    @Column(name = "country")
    String country;
    @Column(name = "full_address")
    String fullAddress;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    LocalDateTime startDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "finish_date")
    LocalDateTime finishDate;

    @Column(name = "is_public")
    boolean publicEvent;
    @Column(name = "confirmation")
    boolean confirmation;

    @Column(name = "limit_participants")
    Integer limitParticipants;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    User owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id")
    @EqualsAndHashCode.Exclude
    Type type;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "event_files",
            joinColumns = @JoinColumn(name="event_id"),
            inverseJoinColumns = @JoinColumn(name="file_id")
    )
    @OrderColumn(name = "file_order")
    @EqualsAndHashCode.Exclude
    List<File> photo;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "event_participants",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @EqualsAndHashCode.Exclude
    Set<User> participants;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "event_tags",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @EqualsAndHashCode.Exclude
    Set<Tag> tags;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "event_languages",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "lang_id")
    )
    @EqualsAndHashCode.Exclude
    Set<Language> language;

    @ManyToMany(mappedBy = "favouriteEvents")
    @EqualsAndHashCode.Exclude
    Set<User> favouriteUsers;

    @Column(name = "active")
    boolean isActive;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "participate_confirmation",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    @EqualsAndHashCode.Exclude
    Set<User> confirmationParticipants;
}
