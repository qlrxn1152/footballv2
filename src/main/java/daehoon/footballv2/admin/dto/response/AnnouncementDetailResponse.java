package daehoon.footballv2.admin.dto.response;

import daehoon.footballv2.admin.domain.AnnouncementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class AnnouncementDetailResponse {

    private Long id;

    private AnnouncementType announcementType;
    private String title;
    private String content;
    private String version;
    private boolean pinned;

    public AnnouncementDetailResponse(Long id, AnnouncementType announcementType, String title, String content, String version, boolean pinned) {
        this.id = id;
        this.announcementType = announcementType;
        this.title = title;
        this.content = content;
        this.version = version;
        this.pinned = pinned;
    }
}
