package daehoon.footballv2.teammatch.service.impl;

import daehoon.footballv2.team.domain.TeamMember;
import daehoon.footballv2.team.validator.TeamValidator;
import daehoon.footballv2.teammatch.domain.TeamMatch;
import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import daehoon.footballv2.teammatch.dto.response.TeamMatchCreateResponse;
import daehoon.footballv2.teammatch.exception.exceptions.DuplicateTeamMatchException;
import daehoon.footballv2.teammatch.repository.TeamMatchRepository;
import daehoon.footballv2.teammatch.service.TeamMatchService;
import daehoon.footballv2.teammatch.validator.TeamMatchValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TeamMatchServiceImpl implements TeamMatchService {

    private final TeamMatchRepository teamMatchRepository;

    private final TeamValidator teamValidator;
    private final TeamMatchValidator teamMatchValidator;


    @Override
    public TeamMatchCreateResponse createTeamMatch(Long teamId, Long memberId) {
        // 매치 생성버튼을 누름 -> 필요한 검증들 진행 ...
        teamValidator.validateTeamExists(teamId); // 팀이있는지
        teamValidator.validateMemberExists(memberId); // 멤버가있는지

        TeamMember teamMember = teamValidator.validateJoinedTeam(memberId); // 해당멤버가, 팀이있는지
        teamValidator.validateSameTeam(teamMember, teamId); // 해당팀에 속해있는게 맞는지

        teamValidator.validateTeamLeader(teamMember); // 해당멤버가 팀장인지

        // 해당팀이 이미 매치를 올린게아닌지 ( 해당팀이 이미 status = PENDING or MATCHED 인 매치 있는지 판단 )
        teamMatchValidator.validateNoActiveMatch(teamId);

        // 매치를 생성 -> 매치를 저장 ( awayTeam = null ) 홈팀에대한 정보만 존재.
        TeamMatch teamMatch = teamMatchRepository.save(new TeamMatch(teamMember.getTeam()));

        // dto 로 변경해서, 응답 dto 를 반환
        return new TeamMatchCreateResponse(
                teamMatch.getId(),
                teamMatch.getHomeTeam().getId(),
                teamMatch.getHomeTeam().getTeamName(),
                teamMatch.getHomeTeam().getTeamRating(),
                teamMatch.getStatus()
        );
    }
}
