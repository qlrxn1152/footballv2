package daehoon.footballv2.notification.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class UnreadNotificationCountResponse {

    private long count;

    public UnreadNotificationCountResponse(long count) {
        this.count = count;
    }
}
