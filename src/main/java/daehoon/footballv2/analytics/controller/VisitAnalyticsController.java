package daehoon.footballv2.analytics.controller;

import daehoon.footballv2.analytics.dto.response.VisitDailySummaryResponse;
import daehoon.footballv2.analytics.service.VisitLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@Slf4j
@RequiredArgsConstructor
public class VisitAnalyticsController {

    private final VisitLogService visitLogService;

    @GetMapping("/api/admin/analytics/visits/daily")
    public ResponseEntity<VisitDailySummaryResponse> getVisitDailySummary(@RequestParam LocalDate date) {

        VisitDailySummaryResponse response = visitLogService.getDailySummary(date);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
