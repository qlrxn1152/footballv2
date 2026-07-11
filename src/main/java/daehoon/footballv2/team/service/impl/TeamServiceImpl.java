package daehoon.footballv2.team.service.impl;

import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.member.exception.exceptions.NotFoundMemberException;
import daehoon.footballv2.member.repository.MemberRepository;
import daehoon.footballv2.team.domain.*;
import daehoon.footballv2.team.dto.response.teamcreate.TeamCreateResponse;
import daehoon.footballv2.team.dto.response.teamdetail.TeamDetailResponse;
import daehoon.footballv2.team.dto.response.teamdisband.TeamDisbandResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestCreateResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestDecisionResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestSummaryResponse;
import daehoon.footballv2.team.dto.response.teamleader.TeamLeaderTransferResponse;
import daehoon.footballv2.team.dto.response.teamlist.TeamSummaryResponse;
import daehoon.footballv2.team.dto.response.teammember.TeamMemberSummaryResponse;
import daehoon.footballv2.team.dto.response.teamname.TeamNameUpdateResponse;
import daehoon.footballv2.team.exception.exceptions.*;
import daehoon.footballv2.team.repository.TeamJoinRequestRepository;
import daehoon.footballv2.team.repository.TeamMemberRepository;
import daehoon.footballv2.team.repository.TeamRepository;
import daehoon.footballv2.team.service.TeamService;
import daehoon.footballv2.team.validator.TeamValidator;
import daehoon.footballv2.team.validator.teamjoin.TeamJoinRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamJoinRequestRepository teamJoinRequestRepository;

    // 검증들
    private final TeamValidator teamValidator;
    private final TeamJoinRequestValidator teamJoinRequestValidator;

    @Override
    public TeamCreateResponse createTeam(String teamName, Long memberId) {
        Member member = teamValidator.validateMemberExists(memberId);

        teamValidator.validateNotJoinedTeam(memberId); // 팀 가입 안되어져있으면 통과
        teamValidator.validateTeamNameNotDuplicate(teamName); // 팀 이름 중복아니면 통과

        // 팀 생성가능한경우 -> 팀 생성 // 멤버를 팀에 속하게
        Team savedTeam = teamRepository.save(new Team(teamName));

        teamMemberRepository.save(new TeamMember(savedTeam, member, TeamRole.LEADER)); // 팀 생성하는경우에는, LEADER 로 배치합니다.

        return new TeamCreateResponse(savedTeam.getId(), savedTeam.getTeamName(), savedTeam.getTeamRating(), member.getId(), member.getUsername());
    }

    @Override
    public TeamJoinRequestCreateResponse joinRequest(Long teamId, Long memberId) {
        Team team = teamValidator.validateTeamExists(teamId);
        Member member = teamValidator.validateMemberExists(memberId);

        teamValidator.validateNotJoinedTeam(memberId);
        teamJoinRequestValidator.validateDuplicateRequest(teamId, memberId); // 중복신청인지 확인

        TeamJoinRequest joinRequest = teamJoinRequestRepository.save(new TeamJoinRequest(team, member));

        return new TeamJoinRequestCreateResponse(
                joinRequest.getId(),
                team.getId(),
                team.getTeamName(),
                member.getId(),
                member.getUsername(),
                joinRequest.getStatus()
        );
    }

    @Override
    public TeamJoinRequestDecisionResponse acceptRequest(Long joinRequestId, Long teamId, Long loginMemberId) {
        TeamJoinRequest joinRequest = teamJoinRequestValidator.validateJoinRequestExists(joinRequestId);
        Team team = teamValidator.validateTeamExists(teamId);
        // 팀장이 가입신청을 수락 -> 팀에 멤버

        teamValidator.validateMemberExists(loginMemberId);
        TeamMember teamLeader = teamValidator.validateJoinedTeam(loginMemberId);

        teamValidator.validateSameTeam(teamLeader, teamId);
        teamValidator.validateTeamLeader(teamLeader);


        // 가입요청에 있는 팀이랑, 해당 팀이랑 같은지
        teamJoinRequestValidator.validateJoinRequestBelongsToTeam(joinRequest, teamId);
        teamJoinRequestValidator.validatePendingStatus(joinRequest);

        // 대상 팀 팀장인거 확인완료. -> 팀에 멤버를 넣어줘야함
        teamValidator.validateNotJoinedTeam(joinRequest.getMember().getId()); // 팀에 가입되어져있지 않으면 -> 통과
        acceptTeamMember(team, joinRequest);

        return new TeamJoinRequestDecisionResponse(
                joinRequest.getId(),
                team.getId(),
                team.getTeamName(),
                joinRequest.getMember().getId(),
                joinRequest.getMember().getUsername(),
                joinRequest.getStatus() // ACCEPTED
        );

    }

    @Override
    public TeamJoinRequestDecisionResponse rejectRequest(Long joinRequestId, Long teamId, Long loginMemberId) {
        TeamJoinRequest joinRequest = teamJoinRequestValidator.validateJoinRequestExists(joinRequestId);
        Team team = teamValidator.validateTeamExists(teamId);
        // 팀장이 가입신청을 거절

        teamValidator.validateMemberExists(loginMemberId);
        TeamMember teamLeader = teamValidator.validateJoinedTeam(loginMemberId);

        teamValidator.validateSameTeam(teamLeader, teamId);
        teamValidator.validateTeamLeader(teamLeader);

        // 가입요청에 있는 팀이랑, 해당 팀이랑 같은지
        teamJoinRequestValidator.validateJoinRequestBelongsToTeam(joinRequest, teamId);
        teamJoinRequestValidator.validatePendingStatus(joinRequest);

        rejectTeamMember(joinRequest);

        return new TeamJoinRequestDecisionResponse(
                joinRequest.getId(),
                team.getId(),
                team.getTeamName(),
                joinRequest.getMember().getId(),
                joinRequest.getMember().getUsername(),
                joinRequest.getStatus()
        );

    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamJoinRequestSummaryResponse> findJoinRequests(Long teamId, Long leaderMemberId, TeamJoinRequestStatus status) {
        teamValidator.validateMemberExists(leaderMemberId);
        teamValidator.validateTeamExists(teamId);
        TeamMember teamLeader = teamValidator.validateJoinedTeam(leaderMemberId);

        teamValidator.validateSameTeam(teamLeader, teamId);
        teamValidator.validateTeamLeader(teamLeader);


        return teamJoinRequestRepository.findAllByTeamIdAndStatusOrderByCreatedAtDesc(teamId, status)
                .stream()
                .map(request -> new TeamJoinRequestSummaryResponse(
                        request.getId(),
                        request.getTeam().getId(),
                        request.getTeam().getTeamName(),
                        request.getMember().getId(),
                        request.getMember().getUsername(),
                        request.getStatus(),
                        request.getCreatedAt()
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamJoinRequestSummaryResponse> findPendingJoinRequests(Long teamId, Long memberId) {
        return findJoinRequests(teamId, memberId, TeamJoinRequestStatus.PENDING)
                .stream()
                .filter(request -> request.getStatus() == TeamJoinRequestStatus.PENDING)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberSummaryResponse> findTeamMembers(Long teamId) {

        teamValidator.validateTeamExists(teamId);

        return teamMemberRepository.findByTeamIdOrderByJoinedAtAsc(teamId)
                .stream()
                .map(teamMember -> new TeamMemberSummaryResponse(
                        teamMember.getId(),
                        teamMember.getTeam().getId(),
                        teamMember.getTeam().getTeamName(),
                        teamMember.getMember().getId(),
                        teamMember.getMember().getUsername(),
                        teamMember.getMember().getMemberRating(),
                        teamMember.getTeamRole(),
                        teamMember.getJoinedAt()
                ))
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public TeamDetailResponse findTeamDetail(Long teamId) {
        Team team = teamValidator.validateTeamExists(teamId);
        int count = teamMemberRepository.countMemberByTeamId(teamId);// 팀에 속한 회원이 몇명인지

        // 리더멤버 조회
        TeamMember leaderMember = teamMemberRepository.findLeaderMemberByTeamIdAndTeamRole(teamId, TeamRole.LEADER) // 해당팀의 리더를 조회 . . .
                .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패"));

        return new TeamDetailResponse(
                team.getId(),
                team.getTeamName(),
                team.getTeamRating(),
                leaderMember.getMember().getId(),
                leaderMember.getMember().getUsername(),
                count,
                team.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamSummaryResponse> findTeams() {

        return teamRepository.findAllByOrderByTeamRatingDesc()
                .stream()
                .map(team -> {
                    int memberCount = teamMemberRepository.countMemberByTeamId(team.getId());
                    TeamMember leaderMember = teamMemberRepository.findLeaderMemberByTeamIdAndTeamRole(team.getId(), TeamRole.LEADER)
                            .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패"));

                    return new TeamSummaryResponse(
                            team.getId(),
                            team.getTeamName(),
                            team.getTeamRating(),
                            leaderMember.getMember().getId(),
                            leaderMember.getMember().getUsername(),
                            memberCount,
                            team.getCreatedAt()
                    );
                })
                .toList();
    }

    @Override
    public TeamLeaderTransferResponse transferLeader(Long teamId, Long oldLeaderMemberId, Long newLeaderMemberId) {
        Team team = teamValidator.validateTeamExists(teamId);
        teamValidator.validateMemberExists(oldLeaderMemberId);
        teamValidator.validateMemberExists(newLeaderMemberId);
        teamValidator.validateSameMemberForTransferLeader(oldLeaderMemberId, newLeaderMemberId);

        TeamMember oldLeaderMember = teamValidator.validateJoinedTeam(oldLeaderMemberId);
        TeamMember newLeaderMember = teamValidator.validateJoinedTeam(newLeaderMemberId);
        teamValidator.validateSameTeam(oldLeaderMember, teamId);
        teamValidator.validateSameTeam(newLeaderMember, teamId);


        teamValidator.validateTeamLeader(oldLeaderMember);
        teamValidator.validateTeamMember(newLeaderMember);


        // 로그인한 사람이 해당팀의 팀장도 맞고, 새로운멤버가 해당팀의 멤버로 있는경우 -> [oldLeaderMember : LEADER -> MEMBER], [newLeaderMember : MEMBER -> LEADER]
        oldLeaderMember.leaderToMember();
        newLeaderMember.memberToLeader();

        return new TeamLeaderTransferResponse(
                team.getId(),
                team.getTeamName(),
                oldLeaderMember.getMember().getId(),
                oldLeaderMember.getMember().getUsername(),
                newLeaderMember.getMember().getId(),
                newLeaderMember.getMember().getUsername()
        );

    }

    @Override
    public TeamNameUpdateResponse updateTeamName(Long teamId, Long leaderMemberId, String newTeamName) {
        Team team = teamValidator.validateTeamExists(teamId);// 팀 조회
        teamValidator.validateMemberExists(leaderMemberId); // 멤버 조회
        TeamMember teamMember = teamValidator.validateJoinedTeam(leaderMemberId);// 팀에 가입되어져있는지

        teamValidator.validateSameTeam(teamMember, teamId);
        teamValidator.validateTeamLeader(teamMember);
        teamValidator.validateSameTeamName(team.getTeamName(), newTeamName);
        teamValidator.validateTeamNameNotDuplicate(newTeamName);

        team.changeTeamName(newTeamName);

        return new TeamNameUpdateResponse(
                team.getId(),
                team.getTeamName(),
                team.getTeamRating(),
                teamMember.getMember().getId(),
                teamMember.getMember().getUsername()
        );
    }

    @Override
    public TeamDisbandResponse disbandTeam(Long teamId, Long leaderMemberId) {
        Team team = teamValidator.validateTeamExists(teamId);
        teamValidator.validateMemberExists(leaderMemberId);
        TeamMember teamMember = teamValidator.validateJoinedTeam(leaderMemberId);
        teamValidator.validateSameTeam(teamMember, teamId);
        teamValidator.validateTeamLeader(teamMember);
        teamValidator.validateCanDisbandTeam(teamId);

        // 가입신청 이력들 삭제
        teamJoinRequestRepository.deleteAllByTeamId(teamId);

        // teamMember 에서, 해당 팀에 있는 TeamMember 들 다 삭제.
        teamMemberRepository.deleteAllByTeamId(teamId);

        // teamRepository -> team 삭제
        teamRepository.delete(team);

        return new TeamDisbandResponse(
                team.getId(),
                team.getTeamName(),
                teamMember.getMember().getId(),
                teamMember.getMember().getUsername(),
                true
        );
    }








    // 비즈니스 로직
    private static void rejectTeamMember(TeamJoinRequest joinRequest) {
        joinRequest.rejectedRequest();
    }

    private void acceptTeamMember(Team team, TeamJoinRequest joinRequest) {
        teamMemberRepository.save(new TeamMember(team, joinRequest.getMember(), TeamRole.MEMBER)); // -> 가입승인
        joinRequest.acceptedRequest(); // status = ACCEPTED
    }




}
