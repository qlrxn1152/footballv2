package daehoon.footballv2.analytics.repository;

import daehoon.footballv2.analytics.domain.VisitEventType;
import daehoon.footballv2.analytics.domain.VisitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {

    List<VisitLog> findByVisitedAtGreaterThanEqualAndVisitedAtLessThan(LocalDateTime start, LocalDateTime end);

    @Query("""
        select count(distinct v.visitorUUID)
        from VisitLog v
        where v.visitedAt >= :start
          and v.visitedAt < :end
        """)
    long countDistinctVisitors(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    long countByEventTypeAndVisitedAtGreaterThanEqualAndVisitedAtLessThan(
            VisitEventType eventType,
            LocalDateTime start,
            LocalDateTime end
    );





}
