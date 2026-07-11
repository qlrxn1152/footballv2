package daehoon.footballv2.teammatch.service.impl;

import daehoon.footballv2.team.domain.TeamMember;
import daehoon.footballv2.team.validator.TeamValidator;
import daehoon.footballv2.teammatch.domain.TeamMatch;
import daehoon.footballv2.teammatch.domain.TeamMatchResult;
import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import daehoon.footballv2.teammatch.dto.response.TeamMatchAcceptResponse;
import daehoon.footballv2.teammatch.dto.response.TeamMatchCreateResponse;
import daehoon.footballv2.teammatch.dto.response.TeamMatchResultResponse;
import daehoon.footballv2.teammatch.dto.response.TeamMatchSummaryResponse;
import daehoon.footballv2.teammatch.repository.TeamMatchRepository;
import daehoon.footballv2.teammatch.repository.TeamMatchResultRepository;
import daehoon.footballv2.teammatch.service.TeamMatchService;
import daehoon.footballv2.teammatch.validator.TeamMatchValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TeamMatchServiceImpl implements TeamMatchService {

    private final TeamMatchRepository teamMatchRepository;
    private final TeamMatchResultRepository teamMatchResultRepository;

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

    @Override
    public TeamMatchAcceptResponse acceptTeamMatch(Long teamMatchId, Long awayLeaderMemberId) {
        TeamMatch teamMatch = teamMatchValidator.validateTeamMatchExists(teamMatchId); // teamMatch 가 있나?
        teamValidator.validateMemberExists(awayLeaderMemberId); // 멤버는있는건가?
        TeamMember awayTeamLeaderMember = teamValidator.validateJoinedTeam(awayLeaderMemberId); // 멤버가 팀이 있는게 맞아 ?
        teamValidator.validateTeamLeader(awayTeamLeaderMember); // 멤버가 팀장인건 맞고?
        teamMatchValidator.validatePendingStatus(teamMatch); // teamMatch 가 PENDING 상태인거 맞나 ?
        teamMatchValidator.validateNotHomeTeam(teamMatch, awayTeamLeaderMember.getTeam().getId()); // 자기팀에 신청하는거 아님?

        teamMatchValidator.validateNoActiveMatchForAccept(awayTeamLeaderMember.getTeam().getId()); // 어웨이팀이 이미 진행중인 매치가 있는게 아니야? -> PENDING, MATCHED 인게 이미 있는거아니야?


        teamMatch.matchedTheMatch(awayTeamLeaderMember.getTeam()); // 매칭성사 -> awayTeam 설정, MATCHED 로 변경

        return new TeamMatchAcceptResponse(
                teamMatch.getId(),
                teamMatch.getHomeTeam().getId(),
                teamMatch.getHomeTeam().getTeamName(),
                teamMatch.getHomeTeam().getTeamRating(),
                awayTeamLeaderMember.getTeam().getId(),
                awayTeamLeaderMember.getTeam().getTeamName(),
                awayTeamLeaderMember.getTeam().getTeamRating(),
                teamMatch.getStatus()
        );

    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMatchSummaryResponse> findTeamMatches() { // 전체조회 ..
        return teamMatchRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(teamMatch -> {
                    if (teamMatch.getAwayTeam() == null) {
                        return new TeamMatchSummaryResponse(
                                teamMatch.getId(),
                                teamMatch.getHomeTeam().getId(),
                                teamMatch.getHomeTeam().getTeamName(),
                                teamMatch.getHomeTeam().getTeamRating(),
                                teamMatch.getStatus(),
                                teamMatch.getCreatedAt());
                    }

                    return new TeamMatchSummaryResponse(
                            teamMatch.getId(),
                            teamMatch.getHomeTeam().getId(),
                            teamMatch.getHomeTeam().getTeamName(),
                            teamMatch.getHomeTeam().getTeamRating(),
                            teamMatch.getAwayTeam().getId(),
                            teamMatch.getAwayTeam().getTeamName(),
                            teamMatch.getAwayTeam().getTeamRating(),
                            teamMatch.getStatus(),
                            teamMatch.getCreatedAt());

                })
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<TeamMatchSummaryResponse> findTeamMatches(TeamMatchStatus status) {

        // status -> PENDING
        if (status == TeamMatchStatus.PENDING) {
            return teamMatchRepository.findAllByStatusOrderByCreatedAtDesc(status)
                    .stream()
                    .map(teamMatch -> new TeamMatchSummaryResponse(
                            teamMatch.getId(),
                            teamMatch.getHomeTeam().getId(),
                            teamMatch.getHomeTeam().getTeamName(),
                            teamMatch.getHomeTeam().getTeamRating(),
                            teamMatch.getStatus(),
                            teamMatch.getCreatedAt()
                    ))
                    .toList();
        }

        // stuats -> MATCHED, COMPLETED .. -> 어웨이팀이 존재
        return teamMatchRepository.findAllByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(teamMatch -> new TeamMatchSummaryResponse(
                        teamMatch.getId(),
                        teamMatch.getHomeTeam().getId(),
                        teamMatch.getHomeTeam().getTeamName(),
                        teamMatch.getHomeTeam().getTeamRating(),
                        teamMatch.getAwayTeam().getId(),
                        teamMatch.getAwayTeam().getTeamName(),
                        teamMatch.getAwayTeam().getTeamRating(),
                        teamMatch.getStatus(),
                        teamMatch.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public TeamMatchResultResponse registerMatchResult(Long teamMatchId, Long homeLeaderMemberId, Integer homeScore, Integer awayScore) {
        TeamMatch teamMatch = teamMatchValidator.validateTeamMatchExists(teamMatchId);
        teamValidator.validateMemberExists(homeLeaderMemberId);

        // 이미 해당매치에 결과가 있으면?
        teamMatchValidator.validateResultNotExists(teamMatch);

        // 매치가 MATCHED 인 상태인게 맞는지
        teamMatchValidator.validateMatchedStatus(teamMatch);

        TeamMember teamMember = teamValidator.validateJoinedTeam(homeLeaderMemberId); // 멤버가 팀에 가입되어져 있나 ?

        teamValidator.validateTeamLeader(teamMember); // 너 팀장임?

        teamMatchValidator.validateParticipantTeam(teamMatch, teamMember.getTeam().getId()); // 결과등록한 멤버 팀이랑, teamMatch 에 홈팀이랑 같음 ?

        // 점수검증
        teamMatchValidator.validateScore(homeScore, awayScore);

        // 팀장까지 맞네 -> 결과입력 ㄱㄱ
        TeamMatchResult matchResult = teamMatchResultRepository.save(new TeamMatchResult(teamMatch, homeScore, awayScore));

        // 매치상태 변경
        teamMatch.completedMatch();

        return new TeamMatchResultResponse(
                teamMatch.getId(),
                teamMatch.getHomeTeam().getId(),
                teamMatch.getHomeTeam().getTeamName(),
                matchResult.getHomeScore(),
                teamMatch.getAwayTeam().getId(),
                teamMatch.getAwayTeam().getTeamName(),
                matchResult.getAwayScore(),
                teamMatch.getStatus()
        );

    }


}
