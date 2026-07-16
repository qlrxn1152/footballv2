package daehoon.footballv2.teampostcommnet.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class TeamPostCommentResponse {

    private Long commentId;
    private Long postId;
    private Long authorMemberId;
    private String authorUsername;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TeamPostCommentResponse(Long commentId, Long postId, Long authorMemberId, String authorUsername, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.commentId = commentId;
        this.postId = postId;
        this.authorMemberId = authorMemberId;
        this.authorUsername = authorUsername;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
