package daehoon.footballv2.teampost.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class TeamPostDetailResponse {

    private Long postId;
    private Long teamId;
    private Long authorMemberId;

    private String title;
    private String content;

    private String authorUsername;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TeamPostDetailResponse(Long postId, Long teamId, Long authorMemberId, String title, String content, String authorUsername, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.postId = postId;
        this.teamId = teamId;
        this.authorMemberId = authorMemberId;
        this.title = title;
        this.content = content;
        this.authorUsername = authorUsername;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
