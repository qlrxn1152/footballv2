package daehoon.footballv2.teampost.validation;

import daehoon.footballv2.teampost.domain.TeamPost;
import daehoon.footballv2.teampost.exception.exceptions.NotFoundTeamPostException;
import daehoon.footballv2.teampost.exception.exceptions.NotSameAuthorMemberException;
import daehoon.footballv2.teampost.exception.exceptions.TeamPostUpdateException;
import daehoon.footballv2.teampost.repository.TeamPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamPostValidator {

    private final TeamPostRepository teamPostRepository;

    public TeamPost validateExistTeamPost(Long postId, Long teamId) {
        return teamPostRepository.findByIdAndTeamId(postId, teamId)
                .orElseThrow(() -> new NotFoundTeamPostException("팀 포스트 조회 실패"));
    }

    public void validateCheckSameAuthor(TeamPost teamPost, Long memberId) {

        if (!teamPost.getAuthorMember().getId().equals(memberId)) {
            throw new NotSameAuthorMemberException("해당 글 작성자가 아닙니다.");
        }
    }

    public void validateTitleForUpdate(String newTitle, String newContent, TeamPost teamPost) {

        if (teamPost.getTitle().equals(newTitle)) {
            throw new TeamPostUpdateException("이전과 같은 제목으로 수정할 수 없습니다.");
        }

        if ( teamPost.getContent().equals(newContent)) {
            throw new TeamPostUpdateException("이전과 같은 내용으로 수정할 수 없습니다.");
        }

    }
}
