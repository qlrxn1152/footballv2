package daehoon.footballv2.analytics.service.impl;

import daehoon.footballv2.analytics.domain.VisitEventType;
import daehoon.footballv2.analytics.domain.VisitLog;
import daehoon.footballv2.analytics.dto.response.VisitDailySummaryResponse;
import daehoon.footballv2.analytics.repository.VisitLogRepository;
import daehoon.footballv2.analytics.service.VisitLogService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class VisitLogServiceImplTest {

    @Autowired private VisitLogService visitLogService;
    @Autowired private VisitLogRepository visitLogRepository;

    @Test
    @DisplayName(value = "방문기록 저장 성공")
    void visitRecord() throws Exception {
        visitLogService.recordVisit(UUID.randomUUID().toString(), "/test", VisitEventType.APP_OPEN);

        List<VisitLog> records = visitLogRepository.findAll();

        assertThat(records).hasSize(1);
        assertThat(records.get(0).getEventType()).isEqualTo(VisitEventType.APP_OPEN);
        assertThat(records.get(0).getVisitorUUID()).isNotNull();
    }

    @Test
    @DisplayName(value = "같은사용자의 요청")
    void visitRecord_sameMember() throws Exception {
        String theMemberUUID = UUID.randomUUID().toString();
        visitLogService.recordVisit(theMemberUUID, "/test", VisitEventType.PAGE_VIEW);
        visitLogService.recordVisit(theMemberUUID, "/test2",  VisitEventType.PAGE_VIEW);
        visitLogService.recordVisit(theMemberUUID, "/test3",  VisitEventType.PAGE_VIEW);

        List<VisitLog> records = visitLogRepository.findAll();

        assertThat(records).hasSize(3);
        assertThat(records).allMatch(log -> log.getVisitorUUID().equals(theMemberUUID));
        assertThat(records).allMatch(log -> log.getEventType().equals(VisitEventType.PAGE_VIEW));
    }

    @Test
    @DisplayName("일별 순 사용자와 페이지 조회 수를 집계한다")
    void dailySummary() {
        LocalDate today = LocalDate.now();
        String firstVisitor = UUID.randomUUID().toString();
        String secondVisitor = UUID.randomUUID().toString();
        visitLogService.recordVisit(firstVisitor, "/", VisitEventType.APP_OPEN);
        visitLogService.recordVisit(firstVisitor, "/api/members", VisitEventType.PAGE_VIEW);
        visitLogService.recordVisit(firstVisitor, "/api/teams", VisitEventType.PAGE_VIEW);
        visitLogService.recordVisit(secondVisitor, "/", VisitEventType.APP_OPEN);
        visitLogService.recordVisit(secondVisitor, "/api/matches", VisitEventType.PAGE_VIEW);

        VisitDailySummaryResponse summary = visitLogService.getDailySummary(today);

        assertThat(summary.getDate()).isEqualTo(today);
        assertThat(summary.getUniqueVisitors()).isEqualTo(2);
        assertThat(summary.getPageViews()).isEqualTo(3);
    }

}
