package daehoon.footballv2.admin.dto.response;

import daehoon.footballv2.admin.domain.AnnouncementType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class AnnouncementSummaryResponse {

    private Long announcementId;
    private AnnouncementType type;
    private String title;
    private String version;
    private boolean pinned;
    private String authorUsername;
    private LocalDateTime createdAt;

    public AnnouncementSummaryResponse(Long announcementId, AnnouncementType type, String title, String version, boolean pinned, String authorUsername, LocalDateTime createdAt) {
        this.announcementId = announcementId;
        this.type = type;
        this.title = title;
        this.version = version;
        this.pinned = pinned;
        this.authorUsername = authorUsername;
        this.createdAt = createdAt;
    }

}
