package daehoon.footballv2.analytics.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
public class VisitDailySummaryResponse {

    private LocalDate date;
    private long uniqueVisitors;
    private long pageViews;

    public VisitDailySummaryResponse(LocalDate date, long uniqueVisitors, long pageViews) {
        this.date = date;
        this.uniqueVisitors = uniqueVisitors;
        this.pageViews = pageViews;
    }
}
