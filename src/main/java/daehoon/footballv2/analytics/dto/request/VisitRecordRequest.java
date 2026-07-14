package daehoon.footballv2.analytics.dto.request;

import daehoon.footballv2.analytics.domain.VisitEventType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter @Setter
@NoArgsConstructor
public class VisitRecordRequest {

    @NotBlank @Length(max = 64)
    private String visitorId;

    @NotBlank @Length(max = 255)
    private String path;

    @NotNull
    private VisitEventType eventType;

    public VisitRecordRequest(String visitorId, String path, VisitEventType eventType) {
        this.visitorId = visitorId;
        this.path = path;
        this.eventType = eventType;
    }
}
