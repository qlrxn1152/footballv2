package daehoon.footballv2.teampost.validation;

import daehoon.footballv2.teampost.domain.TeamPost;
import daehoon.footballv2.teampost.exception.exceptions.NotFoundTeamPostException;
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
}
