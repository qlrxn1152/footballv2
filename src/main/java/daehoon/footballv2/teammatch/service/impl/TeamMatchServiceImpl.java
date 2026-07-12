package daehoon.footballv2.teammatch.service.impl;

import daehoon.footballv2.team.domain.Team;
import daehoon.footballv2.team.domain.TeamMember;
import daehoon.footballv2.team.validator.TeamValidator;
import daehoon.footballv2.teammatch.domain.TeamMatch;
import daehoon.footballv2.teammatch.domain.TeamMatchResult;
import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import daehoon.footballv2.teammatch.dto.response.*;
import daehoon.footballv2.teammatch.exception.exceptions.NotFoundTeamMatchResultException;
import daehoon.footballv2.teammatch.repository.TeamMatchRepository;
import daehoon.footballv2.teammatch.repository.TeamMatchResultRepository;
import daehoon.footballv2.teammatch.service.TeamMatchService;
import daehoon.footballv2.teammatch.validator.TeamMatchValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    // 매치 결과 등록 ...
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

        // 매치상태 변경 , 점수 반영
        teamMatch.completedMatch(matchResult.getHomeScore(), matchResult.getAwayScore());

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







    @Override
    @Transactional(readOnly = true)
    public List<TeamMatchSummaryResponse> findTeamMatches() {
        return teamMatchRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }







    @Override
    @Transactional(readOnly = true) // 현재 매치탭에 들어가면 나오는. .. -> 탭마다 해당하는 status 의 매치들을 가지고옴 ( 전체팀 대상 )
    public List<TeamMatchSummaryResponse> findTeamMatches(TeamMatchStatus status) {

        // status -> PENDING
        if (status == TeamMatchStatus.PENDING) {
            return toPendingSummaryResponse(status);
        }

        // MATCHED -> 매치 결과 점수가 없음.
        if (status == TeamMatchStatus.MATCHED) {
            return toMatchedSummaryResponse(status);
        }

        // COMPLETED -> 결과 확인가능
        return toCompletedSummaryResponse(status);
    }


    @Override
    @Transactional(readOnly = true)
    public List<TeamMatchHistoryResponse> findTeamMatchHistory(Long teamId, TeamMatchStatus status) {

        List<TeamMatchHistoryResponse> historyMatches = new ArrayList<>();

        // teamMatch-> 결과가 있는지를 따진다?
        teamValidator.validateTeamExists(teamId); // 팀 있는지 조회

        if (status == TeamMatchStatus.PENDING) { // 상대팀이 안잡힌 해당팀의 매치들
            return toPendingHistoryResponse(teamId, historyMatches);
        }

        else if (status == TeamMatchStatus.MATCHED) { // 상대팀이 잡힌 해당팀의 매치들
            return toMatchedHistoryResponse(teamId, historyMatches);
        }

        else if (status == TeamMatchStatus.COMPLETED) { // 매치가 종료된 해당팀의 매치들
            return toCompletedHistoryResponse(teamId, historyMatches);
        }

        return historyMatches; // 매치가 없는경우 -> 빈 리스트 반환
    }












    // ========================= 비즈니스 로직 ====================





    // ============= TeamMatchHistory ============

    private @NonNull List<TeamMatchHistoryResponse> toCompletedHistoryResponse(Long teamId, List<TeamMatchHistoryResponse> historyMatches) {
        List<TeamMatch> homeMatches = teamMatchRepository.findByHomeTeamIdAndStatusOrderByCreatedAtDesc(teamId, TeamMatchStatus.COMPLETED);
        homeMatches.forEach(teamMatch -> teamMatchValidator.validateTeamMatchExists(teamMatch.getId())); // 매치가 존재하는지 확인
        homeMatches.forEach(teamMatchValidator::validateCompletedStats); // COMPLETED 상태들이 맞는지 확인
        homeMatches.forEach(teamMatchValidator::validateResultExists);// RESULT 가 있는지 확인

        List<TeamMatch> awayMatches = teamMatchRepository.findByAwayTeamIdAndStatusOrderByCreatedAtDesc(teamId, TeamMatchStatus.COMPLETED);
        awayMatches.forEach(teamMatch -> teamMatchValidator.validateTeamMatchExists(teamMatch.getId())); // 매치가 존재하는지 확인
        awayMatches.forEach(teamMatchValidator::validateCompletedStats); // COMPLETED 상태들이 맞는지 확인
        awayMatches.forEach(teamMatchValidator::validateResultExists);// RESULT 가 있는지 확인

        List<TeamMatchHistoryResponse> homeHistorys = homeMatches.stream()
                .map(teamMatch -> {

                    TeamMatchResult matchResult = teamMatchResultRepository.findByTeamMatchId(teamMatch.getId())
                            .orElseThrow(() -> new NotFoundTeamMatchResultException("매치 결과 조회 실패"));

                    return new TeamMatchHistoryResponse(
                            teamMatch.getId(),
                            teamMatch.getHomeTeam().getId(),
                            teamMatch.getHomeTeam().getTeamName(),
                            teamMatch.getAwayTeam().getId(),
                            teamMatch.getAwayTeam().getTeamName(),
                            teamMatch.getStatus(),
                            teamMatch.getCreatedAt(),
                            matchResult.getHomeScore(),
                            matchResult.getAwayScore(),
                            matchResult.getWinnerTeam() == null ? null : matchResult.getWinnerTeam().getId(),
                            matchResult.getWinnerTeam() == null ? null : matchResult.getWinnerTeam().getTeamName()
                            );
                })
                .toList();

        List<TeamMatchHistoryResponse> awayHistorys = awayMatches.stream()
                .map(teamMatch -> {

                    TeamMatchResult matchResult = teamMatchResultRepository.findByTeamMatchId(teamMatch.getId())
                            .orElseThrow(() -> new NotFoundTeamMatchResultException("매치 결과 조회 실패"));

                        return new TeamMatchHistoryResponse(
                                teamMatch.getId(),
                                teamMatch.getHomeTeam().getId(),
                                teamMatch.getHomeTeam().getTeamName(),
                                teamMatch.getAwayTeam().getId(),
                                teamMatch.getAwayTeam().getTeamName(),
                                teamMatch.getStatus(),
                                teamMatch.getCreatedAt(),
                                matchResult.getHomeScore(),
                                matchResult.getAwayScore(),
                                matchResult.getWinnerTeam() == null ? null : matchResult.getWinnerTeam().getId(),
                                matchResult.getWinnerTeam() == null ? null : matchResult.getWinnerTeam().getTeamName()
                        );
                })
                .toList();

        historyMatches.addAll(homeHistorys);
        historyMatches.addAll(awayHistorys);

        return historyMatches;
    }

    private @NonNull List<TeamMatchHistoryResponse> toMatchedHistoryResponse(Long teamId, List<TeamMatchHistoryResponse> historyMatches) {
        List<TeamMatch> homeMatches = teamMatchRepository.findByHomeTeamIdAndStatusOrderByCreatedAtDesc(teamId, TeamMatchStatus.MATCHED);
        homeMatches.forEach(teamMatch -> teamMatchValidator.validateTeamMatchExists(teamMatch.getId())); // 매치가 존재하는지 확인
        homeMatches.forEach(teamMatchValidator::validateMatchedStatus); // MATCHED 상태들이 맞는지 확인
        homeMatches.forEach(teamMatchValidator::validateResultNotExists); // RESULT 가 없는지 확인

        List<TeamMatch> awayMatches = teamMatchRepository.findByAwayTeamIdAndStatusOrderByCreatedAtDesc(teamId, TeamMatchStatus.MATCHED);
        awayMatches.forEach(teamMatch -> teamMatchValidator.validateTeamMatchExists(teamMatch.getId())); // 매치가 존재하는지 확인
        awayMatches.forEach(teamMatchValidator::validateMatchedStatus); // MATCHED 상태들이 맞는지 확인
        awayMatches.forEach(teamMatchValidator::validateResultNotExists); // RESULT 가 없는지 확인

        List<TeamMatchHistoryResponse> homeHistorys = homeMatches.stream()
                .map(teamMatch -> new TeamMatchHistoryResponse(
                        teamMatch.getId(),
                        teamMatch.getHomeTeam().getId(),
                        teamMatch.getHomeTeam().getTeamName(),
                        teamMatch.getAwayTeam().getId(),
                        teamMatch.getAwayTeam().getTeamName(),
                        teamMatch.getStatus(),
                        teamMatch.getCreatedAt()
                ))
                .toList();

        List<TeamMatchHistoryResponse> awayHistorys = awayMatches.stream()
                .map(teamMatch -> new TeamMatchHistoryResponse(
                        teamMatch.getId(),
                        teamMatch.getHomeTeam().getId(),
                        teamMatch.getHomeTeam().getTeamName(),
                        teamMatch.getAwayTeam().getId(),
                        teamMatch.getAwayTeam().getTeamName(),
                        teamMatch.getStatus(),
                        teamMatch.getCreatedAt()
                ))
                .toList();

        historyMatches.addAll(homeHistorys);
        historyMatches.addAll(awayHistorys);

        return historyMatches;
    }

    private @NonNull List<TeamMatchHistoryResponse> toPendingHistoryResponse(Long teamId, List<TeamMatchHistoryResponse> historyMatches) {
        List<TeamMatch> homeMatches = teamMatchRepository.findByHomeTeamIdAndStatusOrderByCreatedAtDesc(teamId, TeamMatchStatus.PENDING);
        homeMatches.forEach(teamMatch -> teamMatchValidator.validateTeamMatchExists(teamMatch.getId())); // 매치가 존재하는지 확인 -> 현재 무효화되는 ...
        homeMatches.forEach(teamMatchValidator::validatePendingStatus); // PENDING 상태들이 맞는지 확인
        homeMatches.forEach(teamMatchValidator::validateResultNotExists); // RESULT 가 없는지 확인

        List<TeamMatch> awayMatches = teamMatchRepository.findByAwayTeamIdAndStatusOrderByCreatedAtDesc(teamId, TeamMatchStatus.PENDING);
        awayMatches.forEach(teamMatch -> teamMatchValidator.validateTeamMatchExists(teamMatch.getId())); // 매치가 존재하는지 확인
        awayMatches.forEach(teamMatchValidator::validatePendingStatus); // PENDING 상태들이 맞는지 확인
        awayMatches.forEach(teamMatchValidator::validateResultNotExists); // RESULT 가 없는지 확인

        // 다 통과됐음
        List<TeamMatchHistoryResponse> homeHistorys = homeMatches.stream()
                .map(teamMatch -> new TeamMatchHistoryResponse(
                        teamMatch.getId(),
                        teamMatch.getHomeTeam().getId(),
                        teamMatch.getHomeTeam().getTeamName(),
                        teamMatch.getStatus(),
                        teamMatch.getCreatedAt()
                ))
                .toList();


        List<TeamMatchHistoryResponse> awayHistorys =  awayMatches.stream()
                .map(teamMatch -> new TeamMatchHistoryResponse(
                        teamMatch.getId(),
                        teamMatch.getHomeTeam().getId(),
                        teamMatch.getHomeTeam().getTeamName(),
                        teamMatch.getStatus(),
                        teamMatch.getCreatedAt()
                ))
                .toList();

        historyMatches.addAll(homeHistorys);
        historyMatches.addAll(awayHistorys);

        return historyMatches;
    }

    // ========== TeamMatchSummary ===========
    private @NonNull List<TeamMatchSummaryResponse> toCompletedSummaryResponse(TeamMatchStatus status) {
        return teamMatchRepository.findAllByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(teamMatch -> {
                    TeamMatchResult matchResult = teamMatchResultRepository.findByTeamMatchId(teamMatch.getId())
                            .orElseThrow(() -> new NotFoundTeamMatchResultException("매치 결과 조회 실패"));


                    return new TeamMatchSummaryResponse(
                            teamMatch.getId(),
                            teamMatch.getHomeTeam().getId(),
                            teamMatch.getHomeTeam().getTeamName(),
                            teamMatch.getHomeTeam().getTeamRating(),
                            matchResult.getHomeScore(),

                            teamMatch.getAwayTeam().getId(),
                            teamMatch.getAwayTeam().getTeamName(),
                            teamMatch.getAwayTeam().getTeamRating(),
                            matchResult.getAwayScore(),

                            matchResult.getWinnerTeam() == null ? null : matchResult.getWinnerTeam().getId(),
                            matchResult.getWinnerTeam() == null ? null : matchResult.getWinnerTeam().getTeamName(),

                            teamMatch.getStatus(),
                            teamMatch.getCreatedAt()
                    );
                })
                .toList();
    }

    private @NonNull List<TeamMatchSummaryResponse> toMatchedSummaryResponse(TeamMatchStatus status) {
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

    private @NonNull List<TeamMatchSummaryResponse> toPendingSummaryResponse(TeamMatchStatus status) {
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


    private TeamMatchSummaryResponse toSummaryResponse(TeamMatch teamMatch) {
        if (teamMatch.getStatus() == TeamMatchStatus.PENDING) {
            return new TeamMatchSummaryResponse(
                    teamMatch.getId(),
                    teamMatch.getHomeTeam().getId(),
                    teamMatch.getHomeTeam().getTeamName(),
                    teamMatch.getHomeTeam().getTeamRating(),
                    teamMatch.getStatus(),
                    teamMatch.getCreatedAt()
            );
        }

        if (teamMatch.getStatus() == TeamMatchStatus.MATCHED) {
            return new TeamMatchSummaryResponse(
                    teamMatch.getId(),
                    teamMatch.getHomeTeam().getId(),
                    teamMatch.getHomeTeam().getTeamName(),
                    teamMatch.getHomeTeam().getTeamRating(),
                    teamMatch.getAwayTeam().getId(),
                    teamMatch.getAwayTeam().getTeamName(),
                    teamMatch.getAwayTeam().getTeamRating(),
                    teamMatch.getStatus(),
                    teamMatch.getCreatedAt()
            );
        }

        TeamMatchResult matchResult = teamMatchResultRepository.findByTeamMatchId(teamMatch.getId())
                .orElseThrow(() -> new NotFoundTeamMatchResultException("매치 결과 조회 실패"));

        return new TeamMatchSummaryResponse(
                teamMatch.getId(),
                teamMatch.getHomeTeam().getId(),
                teamMatch.getHomeTeam().getTeamName(),
                teamMatch.getHomeTeam().getTeamRating(),
                matchResult.getHomeScore(),
                teamMatch.getAwayTeam().getId(),
                teamMatch.getAwayTeam().getTeamName(),
                teamMatch.getAwayTeam().getTeamRating(),
                matchResult.getAwayScore(),
                matchResult.getWinnerTeam() == null ? null : matchResult.getWinnerTeam().getId(),
                matchResult.getWinnerTeam() == null ? null : matchResult.getWinnerTeam().getTeamName(),
                teamMatch.getStatus(),
                teamMatch.getCreatedAt()
        );
    }


}
