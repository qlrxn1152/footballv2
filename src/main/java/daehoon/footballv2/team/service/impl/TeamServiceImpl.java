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

    private final TeamValidator teamValidator;

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
        // 팀장이 가입신청을 수락 -> 팀에 멤버
        TeamJoinRequest joinRequest = teamJoinRequestRepository.findById(joinRequestId)
                .orElseThrow(() -> new NotFoundTeamJoinRequestException("가입신청 조회 실패"));

        TeamMember teamLeader = teamMemberRepository.findByMemberId(loginMemberId)
                .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundTeamException("팀 조회 실패"));

        // 가입요청에 있는 팀이랑, 해당 팀이랑 같은지
        validateForTeamJoinRequestAcceptOrReject(teamId, joinRequest, teamLeader);


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
        TeamJoinRequest joinRequest = teamJoinRequestRepository.findById(joinRequestId)
                .orElseThrow(() -> new NotFoundTeamJoinRequestException("가입신청 조회 실패"));

        TeamMember teamLeader = teamMemberRepository.findByMemberId(loginMemberId)
                .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundTeamException("팀 조회 실패"));

        validateForTeamJoinRequestAcceptOrReject(teamId, joinRequest, teamLeader);
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
    public TeamLeaderTransferResponse transferLeader(Long teamId, Long currentLeaderMemberId, Long newLeaderMemberId) {
        // 팀이 있나?
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundTeamException("팀 조회 실패"));

        // 멤버들은 있나 ?
        Member oldMember = memberRepository.findById(currentLeaderMemberId)
                .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패"));

        Member newMember = memberRepository.findById(newLeaderMemberId)
                .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패"));

        // oldMember -> 해당팀에 속해있나?
        TeamMember oldLeaderMember = teamMemberRepository.findByMemberId(oldMember.getId())
                .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패"));
        if (oldLeaderMember.getTeam() != team) {
            throw new NotJoinedTeamException("해당 팀의 멤버가 아닙니다.");
        }

        // oldMember -> 팀장이 맞나 ?
        if ( oldLeaderMember.getTeamRole() !=  TeamRole.LEADER) {
            throw new NotTeamLeaderException("팀장이 아닙니다.");
        }


        // newMember -> 해당팀에 속해있나 ?
        TeamMember newLeaderMember = teamMemberRepository.findByMemberId(newMember.getId())
                .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패"));
        if (newLeaderMember.getTeam() != team) {
            throw new NotJoinedTeamException("해당 팀의 멤버가 아닙니다.");
        }

        if (currentLeaderMemberId.equals(newLeaderMemberId)) {
            throw new IllegalArgumentException("같은 회원으로는 변경이 불가능합니다.");
        }

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
        // 팀조회, 팀장맞는지 ..
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundTeamException("팀 조회 실패"));

        memberRepository.findById(leaderMemberId)
                .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패"));

        TeamMember teamMember = teamMemberRepository.findByMemberId(leaderMemberId)
                .orElseThrow(() -> new NotJoinedTeamException("팀에 속한 멤버아닙니다."));

        if (teamMember.getTeam() != team) {
            throw new NotJoinedTeamException("다른팀 소속입니다.");
        }

        if ( teamMember.getTeamRole() !=  TeamRole.LEADER) {
            throw new NotTeamLeaderException("팀장이 아닙니다.");
        }

        // 팀 멤버가 1명뿐인게 맞나 ? , 1명이면 자기자신이 맞나? ( 팀리더만 남은거 맞음? )
        int memberCount = teamMemberRepository.countMemberByTeamId(teamId);


        if (memberCount != 1) {
            // 1명이 아님.
            throw new CannotDisbandTeamException("팀 해체를 위해서는 팀원이 1명이여야 합니다.");
        }

        if (!teamMember.getMember().getId().equals(leaderMemberId)) {
            // 너가 속한게 아닌데?
            throw new CannotDisbandTeamException("자신의 팀만 해체할수있습니다.");
        }

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
        joinRequest.acceptedRequest();
    }

    private static void validateForTeamJoinRequestAcceptOrReject(Long teamId, TeamJoinRequest joinRequest, TeamMember teamLeader) {
        // 가입요청에 있는 팀이랑, 해당 팀이랑 같은지
        if (!joinRequest.getTeam().getId().equals(teamId)) {
            throw new NotSameTeamException("팀이 다릅니다.");
        }

        // PENDING 이 아닌, ACCEPTED, REJECTED -> 즉, 이미 가입신청을 승인했거나 거절한경우.
        if (!joinRequest.getStatus().equals(TeamJoinRequestStatus.PENDING)) {
            throw new TeamJoinRequestException("이미 가입신청을 승인 / 거절한 요청입니다.");
        }

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
