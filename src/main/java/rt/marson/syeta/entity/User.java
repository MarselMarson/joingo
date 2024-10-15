package rt.marson.syeta.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "firstname")
    String firstName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "birth_date")
    LocalDateTime birthDate;

    @Column(name = "lastname")
    String lastName;

    @Column(name = "password")
    String password;

    @Column(name = "email")
    String email;

    @Column(name = "description")
    String description;

    @Column(name = "active")
    boolean isActive;

    @Column(name = "is_email_verified")
    boolean isEmailVerified;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "user_files",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    @EqualsAndHashCode.Exclude
    File photo;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    @EqualsAndHashCode.Exclude
    Country country;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    Role role;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "user_favourite_events",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    @EqualsAndHashCode.Exclude
    Set<Event> favouriteEvents;

    @ManyToMany(mappedBy = "participants")
    @EqualsAndHashCode.Exclude
    Set<Event> participateEvents;

    @OneToMany(mappedBy = "owner")
    @EqualsAndHashCode.Exclude
    Set<Event> createdEvents;

    @ManyToMany(mappedBy = "confirmationParticipants")
    @EqualsAndHashCode.Exclude
    Set<Event> confirmationRequest;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    public void addFavouriteEvent(Event event) {
        this.favouriteEvents.add(event);
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
