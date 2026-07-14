package daehoon.footballv2.analytics.controller;

import daehoon.footballv2.analytics.dto.response.VisitDailySummaryResponse;
import daehoon.footballv2.analytics.service.VisitLogService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class VisitAnalyticsControllerTest {

    @Test
    void dailySummaryAcceptsDateQueryParameter() throws Exception {
        VisitLogService visitLogService = mock(VisitLogService.class);
        LocalDate date = LocalDate.of(2026, 7, 14);
        given(visitLogService.getDailySummary(date))
                .willReturn(new VisitDailySummaryResponse(date, 8, 37));
        MockMvc mockMvc = standaloneSetup(
                new VisitAnalyticsController(visitLogService)
        ).build();

        mockMvc.perform(get("/api/admin/analytics/visits/daily")
                        .param("date", "2026-07-14"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-07-14"))
                .andExpect(jsonPath("$.uniqueVisitors").value(8))
                .andExpect(jsonPath("$.pageViews").value(37));

        verify(visitLogService).getDailySummary(date);
    }
}
