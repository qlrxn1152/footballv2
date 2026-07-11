package daehoon.footballv2.teammatch.validator;

import daehoon.footballv2.teammatch.domain.TeamMatch;
import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import daehoon.footballv2.teammatch.exception.exceptions.*;
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

    public TeamMatch validateTeamMatchExists(Long teamMatchId) {
        return teamMatchRepository.findById(teamMatchId)
                .orElseThrow(() -> new NotFoundTeamMatchException("매치 조회 실패"));
    }

    public void validatePendingStatus(TeamMatch teamMatch) {
        if (teamMatch.getStatus() != TeamMatchStatus.PENDING) {
            throw new NotPendingTeamMatchException("대기 중인 매치만 수락할 수 있습니다.");
        }
    }

    public void validateNoActiveMatchForAccept(Long awayTeamId) {
        if (teamMatchRepository.existsByHomeTeamIdAndStatus(awayTeamId, TeamMatchStatus.PENDING) || teamMatchRepository.existsByHomeTeamIdAndStatus(awayTeamId, TeamMatchStatus.MATCHED)) {
            throw new AlreadyExistTeamMatchException("이미 진행중인 매치가 있습니다.");
        }
    }

    public void validateNotHomeTeam(TeamMatch teamMatch, Long awayTeamId) {
        if (teamMatch.getHomeTeam().getId().equals(awayTeamId)) {
            throw new CaannotAcceptOwnTeamMatchException("자기팀이 신청한 매치에는 수락을 할 수 없습니다.");
        }
    }

}
