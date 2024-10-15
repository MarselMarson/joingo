package rt.marson.syeta.entity.chat;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import rt.marson.syeta.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tetatet_chats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user1_id")
    User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id")
    User user2;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    @OrderBy("id")
    @EqualsAndHashCode.Exclude
    List<ChatMessage> messages;

    @OneToOne
    @JoinColumn(name = "last_message_id")
    @EqualsAndHashCode.Exclude
    ChatMessage lastMessage;

    @Column(name = "active")
    boolean active;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
