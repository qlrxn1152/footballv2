package daehoon.footballv2.analytics.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "visit_log")
public class VisitLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_log_id")
    private Long id;

    @Column(name = "visitor_uuid", nullable = false, length = 64)
    private String visitorUUID;

    @Column(nullable = false)
    private String path;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private VisitEventType eventType;

    @Column(name = "visited_at", nullable = false)
    private Instant visitedAt;

    public VisitLog(String visitorUUID, String path, VisitEventType eventType) {
        this.visitorUUID = visitorUUID;
        this.path = path;
        this.eventType = eventType;
        this.visitedAt = Instant.now();
    }

}
