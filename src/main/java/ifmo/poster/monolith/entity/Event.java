package ifmo.poster.monolith.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import ifmo.poster.monolith.enums.EventStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Size(min = 5, max = 100)
    @Column(name = "event_name", nullable = false, length = 100)
    private String eventName;

    @NotBlank
    @Size(min = 50, max = 200)
    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @NotBlank
    @Size(min = 5, max = 200)
    @Column(name = "location", nullable = false, length = 200)
    private String location;

    @NotNull
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_tags",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EventStatus status = EventStatus.PENDING;
    
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private Set<Ticket> tickets;
}