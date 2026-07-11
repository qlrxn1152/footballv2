package daehoon.footballv2.team.validator.teamjoin;

import daehoon.footballv2.team.domain.TeamJoinRequest;
import daehoon.footballv2.team.domain.TeamJoinRequestStatus;
import daehoon.footballv2.team.exception.exceptions.NotFoundTeamJoinRequestException;
import daehoon.footballv2.team.exception.exceptions.NotSameTeamException;
import daehoon.footballv2.team.exception.exceptions.TeamJoinRequestException;
import daehoon.footballv2.team.repository.TeamJoinRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TeamJoinRequestValidator {

    private final TeamJoinRequestRepository teamJoinRequestRepository;


    /**
     * 파라미터로 들어온 teamJoinRequestId 로 팀 가입신청을 조회해서 해당 가입신청이 존재하는지 판단하는 메서드입니다.
     * @param teamJoinRequestId
     * @return 해당팀 가입신청 조회 성공시, 가입신청 객체를 반환하고 / 조회실패시 NotFoundTeamJoinRequestException 예외를 터트립니다.
     */
    public TeamJoinRequest validateJoinRequestExists(Long teamJoinRequestId) {
        return teamJoinRequestRepository.findById(teamJoinRequestId)
                .orElseThrow(() -> new NotFoundTeamJoinRequestException("가입신청 조회 실패"));
    }

    /**
     * teamId 와, 가입신청에 있는 팀이 같은 아이디를 가지는지 즉, 같은팀인지 조회하는 메서드입니다.
     * @param teamJoinRequest
     * @param teamId
     * @return 같은팀일경우 통과, 같은팀이 아닌경우 -> NotSameTeamException 을 던집니다.
     */
    public void validateJoinRequestBelongsToTeam(TeamJoinRequest teamJoinRequest, Long teamId) {
        if (!teamJoinRequest.getTeam().getId().equals(teamId)) {
            throw new NotSameTeamException("팀이 다릅니다.");
        }
    }

    /**
     * 가입신청의 status = PENDING 인지 확인하는 메서드입니다.
     * @param teamJoinRequest
     * @return PENDING -> 통과 / PENDING 이 아닌 ( ACCEPTED, REJECTED ) 경우, TeamJoinRequestException 을 던집니다.
     */
    public void validatePendingStatus(TeamJoinRequest teamJoinRequest) {
        if ( teamJoinRequest.getStatus() !=  TeamJoinRequestStatus.PENDING) {
            throw new TeamJoinRequestException("이미 가입신청을 승인 / 거절한 요청입니다.");
        }
    }
}
