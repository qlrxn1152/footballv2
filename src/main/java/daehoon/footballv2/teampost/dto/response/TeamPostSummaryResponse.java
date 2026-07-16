package daehoon.footballv2.teampost.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class TeamPostSummaryResponse {

    private Long postId;
    private Long teamId;
    private Long authorMemberId;

    private String title;

    private String authorUsername;
    private LocalDateTime createdAt;

    public TeamPostSummaryResponse(Long postId, Long teamId, Long authorMemberId, String title, String authorUsername, LocalDateTime createdAt) {
        this.postId = postId;
        this.teamId = teamId;
        this.authorMemberId = authorMemberId;
        this.title = title;
        this.authorUsername = authorUsername;
        this.createdAt = createdAt;
    }
}
