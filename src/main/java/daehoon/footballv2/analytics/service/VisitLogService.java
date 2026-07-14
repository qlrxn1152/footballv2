package daehoon.footballv2.analytics.service;

import daehoon.footballv2.analytics.domain.VisitEventType;
import daehoon.footballv2.analytics.dto.response.VisitDailySummaryResponse;
import daehoon.footballv2.analytics.dto.response.VisitRecordResponse;

import java.time.LocalDate;

public interface VisitLogService {

    VisitRecordResponse recordVisit(String visitorId, String path, VisitEventType eventType);

    VisitDailySummaryResponse getDailySummary(LocalDate date);
}
