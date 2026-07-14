package daehoon.footballv2.analytics.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter @Setter
@NoArgsConstructor
public class VisitRecordResponse {

    private Long visitId;
    private LocalDateTime visitedAt;

    public VisitRecordResponse(Long visitId, LocalDateTime visitedAt) {
        this.visitId = visitId;
        this.visitedAt = visitedAt;
    }
}
