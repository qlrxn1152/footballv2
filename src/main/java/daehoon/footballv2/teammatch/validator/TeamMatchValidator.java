package daehoon.footballv2.teammatch.validator;

import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import daehoon.footballv2.teammatch.exception.exceptions.DuplicateTeamMatchException;
import daehoon.footballv2.teammatch.repository.TeamMatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TeamMatchValidator {

    private final TeamMatchRepository teamMatchRepository;

    /**
     * 해당팀이 이미 매치를 올린게아닌지 즉, 매치가 이미 진행중인게 아닌지 판단. ( 해당팀이 이미 status = PENDING 또는, MATCHED 인 매치가 있는지 판단 )
     * @param teamId
     * @return 해당팀이 진행하는 매치의 status 가 아직, PENDING 또는, MATCHED 인 경우 DuplicateTeamMatchException 을 던지고, status = COMPLETED 이거나, 매치등록을 안했으면 통과
     */
    public void validateNoActiveMatch(Long teamId) {
        if (teamMatchRepository.existsByHomeTeamIdAndStatus(teamId, TeamMatchStatus.PENDING)) {
            throw new DuplicateTeamMatchException("이미 등록된 매치요청이 존재합니다.");
        }

        else if (teamMatchRepository.existsByHomeTeamIdAndStatus(teamId, TeamMatchStatus.MATCHED)) {
            throw new DuplicateTeamMatchException("이미 진행중인 매치가 존재합니다.");
        }
    }

}
