package daehoon.footballv2.analytics.controller;

import daehoon.footballv2.analytics.dto.request.VisitRecordRequest;
import daehoon.footballv2.analytics.dto.response.VisitRecordResponse;
import daehoon.footballv2.analytics.service.VisitLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class VisitorLogController {

    private final VisitLogService visitLogService;

    @PostMapping("/api/analytics/visits")
    public ResponseEntity<VisitRecordResponse> recordVisit(@Valid @RequestBody VisitRecordRequest request) {

        VisitRecordResponse response = visitLogService.recordVisit(request.getVisitorId(), request.getPath(), request.getEventType());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
}
