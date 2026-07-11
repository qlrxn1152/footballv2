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
    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamJoinRequestRepository teamJoinRequestRepository;

    // 검증들
    private final TeamValidator teamValidator;
    private final TeamJoinRequestValidator teamJoinRequestValidator;

    // 생성요청 ->
    @Override
    public TeamCreateResponse createTeam(String teamName, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패"));

        validateForCrateTeam(teamName, memberId);

        // 팀 생성가능한경우 -> 팀 생성 // 멤버를 팀에 속하게
        Team savedTeam = teamRepository.save(new Team(teamName));

        teamMemberRepository.save(new TeamMember(savedTeam, member, TeamRole.LEADER)); // 팀 생성하는경우에는, LEADER 로 배치합니다.

        return new TeamCreateResponse(savedTeam.getId(), savedTeam.getTeamName(), savedTeam.getTeamRating(), member.getId(), member.getUsername());
    }

    @Override
    public TeamJoinRequestCreateResponse joinRequest(Long teamId, Long memberId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundTeamException("팀 조회 실패"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패"));

        validateForTeamJoinRequestCreate(teamId, memberId);
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

        TeamMember teamLeader = teamMemberRepository.findByMemberId(leaderMemberId)
                .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패"));

        // 팀장인지 조회 -> 우선 로그인한 사람이 팀 있는건 맞음? , 팀이있다면, 해당팀의 id랑 teamId 가 같나?, 같으면 해당팀 팀장이 맞나?
        if (teamLeader.getTeam() == null) {
            throw new NotJoinedTeamException("팀에 속해있지 않습니다.");
        }

        if (!teamLeader.getTeam().getId().equals(teamId)) {
            throw new NotJoinedTeamException("다른팀 소속입니다.");
        }

        if (!teamLeader.getTeamRole().equals(TeamRole.LEADER)) {
            throw new NotTeamLeaderException("팀장이 아닙니다.");
        }

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

        teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundTeamException("팀 조회 실패"));

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
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundTeamException("팀 조회 실패"));

        int count = teamMemberRepository.countMemberByTeamId(teamId);// 팀에 속한 회원이 몇명인지
        TeamMember leaderMember = teamMemberRepository.findLeaderMemberByTeamIdAndTeamRole(teamId, TeamRole.LEADER)
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








    // 비즈니스 로직 // => // TODO : validator 로 코드 수정후, 아래 검증로직들 삭제
    private void validateForTeamJoinRequestCreate(Long teamId, Long memberId) {
        // 팀에 이미 가입되어져있는지 ?
        if (teamMemberRepository.existsByMemberId(memberId)) {
            throw new AlreadyJoinedTeamException("이미 팀에 소속된 회원입니다.");
        }

        // 이미 같은팀에 PENDING 가입신청이 있는지?
        if (teamJoinRequestRepository.existsByTeamIdAndMemberIdAndStatus(teamId, memberId, TeamJoinRequestStatus.PENDING)) {
            throw new DuplicateTeamJoinRequestException("이미 가입신청한 팀입니다.");
        }
    }

    private static void rejectTeamMember(TeamJoinRequest joinRequest) {
        joinRequest.rejectedRequest();
    }

    private void acceptTeamMember(Team team, TeamJoinRequest joinRequest) {
        teamMemberRepository.save(new TeamMember(team, joinRequest.getMember(), TeamRole.MEMBER)); // -> 가입승인
        joinRequest.acceptedRequest(); // status = ACCEPTED
    }


    private void validateForCrateTeam(String teamName, Long memberId) {
        // 팀 이름이 이미 존재하는지 ?
        if (teamRepository.existsByTeamName(teamName)) {
            throw new DuplicateTeamNameException("팀 이름 중복");
        }

        // 팀에 이미 가입되어져있는지 ?
        if (teamMemberRepository.existsByMemberId(memberId)) {
            throw new AlreadyJoinedTeamException("이미 팀에 소속된 회원입니다.");
        }
    }



}
