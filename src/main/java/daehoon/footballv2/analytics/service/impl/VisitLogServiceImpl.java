package daehoon.footballv2.analytics.service.impl;

import daehoon.footballv2.analytics.domain.VisitEventType;
import daehoon.footballv2.analytics.domain.VisitLog;
import daehoon.footballv2.analytics.dto.response.VisitDailySummaryResponse;
import daehoon.footballv2.analytics.dto.response.VisitRecordResponse;
import daehoon.footballv2.analytics.repository.VisitLogRepository;
import daehoon.footballv2.analytics.service.VisitLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class VisitLogServiceImpl implements VisitLogService {

    private final VisitLogRepository visitLogRepository;

    @Override
    public VisitRecordResponse recordVisit(String visitorId, String path, VisitEventType eventType) {

        VisitLog log = new VisitLog(visitorId, path, eventType);
        VisitLog saved = visitLogRepository.save(log);

        return new VisitRecordResponse(saved.getId(), saved.getVisitedAt());
    }

    @Override
    public VisitDailySummaryResponse getDailySummary(LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();


        long uniqueVisitors = visitLogRepository.countDistinctVisitors(start, end);
        long pageViews = visitLogRepository.countByEventTypeAndVisitedAtGreaterThanEqualAndVisitedAtLessThan(VisitEventType.PAGE_VIEW, start, end);

        // 해당 날짜만 ...
        return new VisitDailySummaryResponse(date, uniqueVisitors, pageViews);
    }


}
