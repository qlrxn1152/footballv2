package daehoon.footballv2.team.service.impl;

import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.member.exception.NotFoundMemberException;
import daehoon.footballv2.member.repository.MemberRepository;
import daehoon.footballv2.team.domain.*;
import daehoon.footballv2.team.dto.response.teamcreate.TeamCreateResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestCreateResponse;
import daehoon.footballv2.team.exception.exceptions.AlreadyJoinedTeamException;
import daehoon.footballv2.team.exception.exceptions.DuplicateTeamJoinRequestException;
import daehoon.footballv2.team.exception.exceptions.DuplicateTeamNameException;
import daehoon.footballv2.team.exception.exceptions.NotFoundTeamException;
import daehoon.footballv2.team.repository.TeamJoinRequestRepository;
import daehoon.footballv2.team.repository.TeamMemberRepository;
import daehoon.footballv2.team.repository.TeamRepository;
import daehoon.footballv2.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamJoinRequestRepository teamJoinRequestRepository;

    // 생성요청 ->
    @Override
    public TeamCreateResponse createTeam(String teamName, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패"));

        // 팀 이름이 이미 존재하는지 ?
        if (teamRepository.existsByTeamName(teamName)) {
            throw new DuplicateTeamNameException("팀 이름 중복");
        }

        // 팀에 이미 가입되어져있는지 ?
        if (teamMemberRepository.existsByMemberId(memberId)) {
            throw new AlreadyJoinedTeamException("이미 팀에 소속된 회원입니다.");
        }

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

        // 팀에 이미 가입되어져있는지 ?
        if (teamMemberRepository.existsByMemberId(memberId)) {
            throw new AlreadyJoinedTeamException("이미 팀에 소속된 회원입니다.");
        }

        // 이미 같은팀에 PENDING 가입신청이 있는지?
        if (teamJoinRequestRepository.existsByTeamIdAndMemberIdAndStatus(teamId, memberId, TeamJoinRequestStatus.PENDING)) {
            throw new DuplicateTeamJoinRequestException("이미 가입신청한 팀입니다.");
        }

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




}
