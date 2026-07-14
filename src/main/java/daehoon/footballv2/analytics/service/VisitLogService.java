package daehoon.footballv2.analytics.service;

import daehoon.footballv2.analytics.domain.VisitEventType;
import daehoon.footballv2.analytics.dto.response.VisitRecordResponse;

public interface VisitLogService {

    VisitRecordResponse recordVisit(String visitorId, String path, VisitEventType eventType);
}
