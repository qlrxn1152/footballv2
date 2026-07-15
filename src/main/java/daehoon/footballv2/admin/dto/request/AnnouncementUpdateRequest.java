package daehoon.footballv2.admin.dto.request;

import daehoon.footballv2.admin.domain.AnnouncementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class AnnouncementUpdateRequest {

    @NotNull
    private AnnouncementType announcementType;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String version;

    private boolean pinned; // 고정할건지: 고정 -> true

    public AnnouncementUpdateRequest(AnnouncementType announcementType, String title, String content, String version, boolean pinned) {
        this.announcementType = announcementType;
        this.title = title;
        this.content = content;
        this.version = version;
        this.pinned = pinned;
    }
}
