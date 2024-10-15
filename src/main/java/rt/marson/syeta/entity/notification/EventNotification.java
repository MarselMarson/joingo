package rt.marson.syeta.entity.notification;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import rt.marson.syeta.entity.Event;
import rt.marson.syeta.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "target_id")
    User target;
    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;

    @Column(name = "active")
    Boolean isActive;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
