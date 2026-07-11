package daehoon.footballv2.team.validator;

import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.member.exception.exceptions.NotFoundMemberException;
import daehoon.footballv2.member.repository.MemberRepository;
import daehoon.footballv2.team.domain.Team;
import daehoon.footballv2.team.domain.TeamMember;
import daehoon.footballv2.team.domain.TeamRole;
import daehoon.footballv2.team.exception.exceptions.*;
import daehoon.footballv2.team.repository.TeamMemberRepository;
import daehoon.footballv2.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TeamValidator {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;

    /**
     * 파라미터로 들어온 teamId 로 팀을 조회해서 팀이 존재하는지 판단하는 메서드입니다.
     * @param teamId
     * @return 해당팀 조회 성공시, 팀 객체를 반환하고 / 조회실패시 NotFoundTeamException 예외를 터트립니다.
     */
    public Team validateTeamExists(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundTeamException("팀 조회 실패"));
    }

    /**
     * 파라미터로 들어온 memberId 로 멤버를 조회해서 멤버가 존재하는지 판단하는 메서드입니다.
     * @param memberId
     * @return 해당팀 조회 성공시, 멤버 객체를 반환하고 / 조회실패시 NotFoundMemberException 예외를 터트립니다.
     */
    public Member validateMemberExists(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패"));
    }

    /**
     * 파라미터로 들어온 memberId 로 멤버를 조회해서 해당 멤버가 팀에 속해있는지 판단하는 메서드입니다.
     * @param memberId
     * @return 해당팀 조회 성공시, 팀멤버 객체를 반환하고 / 조회실패시 NotJoinedTeamException 예외를 터트립니다.
     */
    public TeamMember validateJoinedTeam(Long memberId) {
        return teamMemberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new NotJoinedTeamException("팀에 속해있지 않는 멤버입니다."));
    }


    /**
     * teamMember 객체안에있는 팀과, 또 다른 파라미터로 들어온 teamId 가 같은지 조회합니다.
     * 즉, 해당 팀에 속해있는 멤버가 파라미터로 들어온 TeamId 소속인지 판단합니다.
     * @param teamMember
     * @param teamId
     * @return 같은팀이면 통과하고, 다른 팀이면 NotSameTeamException 을 던집니다.
     */
    public void validateSameTeam(TeamMember teamMember, Long teamId) {
        if (!teamMember.getTeam().getId().equals(teamId)) {
            throw new NotSameTeamException("다른팀 소속입니다.");
        }
    }

    /**
     * 해당 teamMember 객체안에 있는 멤버가, 해당팀의 팀장인지 판단하는 메서드입니다.
     * @param teamMember
     * @return 팀장이 아닌경우, NotTeamLeaderException 예외를 던집니다.
     */
    public void validateTeamLeader(TeamMember teamMember) {
        if (teamMember.getTeamRole() != TeamRole.LEADER) {
            throw new NotTeamLeaderException("팀장이 아닙니다.");
        }
    }

    /**
     * 팀 이름이 중복인지 확인하는 메서드입니다. ( 이미 teamName 을 팀이름으로 가진 팀이 있는지 판단합니다. )
     * @param teamName
     * @return 팀 이름이 이미 존재 -> DuplicateTeamNameException 예외를 던집니다.
     */
    public void validateTeamNameNotDuplicate(String teamName) {
        if (teamRepository.existsByTeamName(teamName)) {
            throw new DuplicateTeamNameException("팀 이름 중복");
        }
    }

    /**
     * 팀 해체를 위해서, 해당팀에 멤버가 1명뿐인지 판단하는 메서드입니다.
     * @param teamId
     * @return 팀 멤버가 1명이 아니면 -> CannotDisbandTeamException 예외를 던집니다.
     */
    public void validateCanDisbandTeam(Long teamId) {
        int memberCount = teamMemberRepository.countMemberByTeamId(teamId);

        if (memberCount != 1) {
            throw new CannotDisbandTeamException("팀 해체를 위해서는 팀장자신인 1명뿐이여야합니다.");
        }


    }


    /**
     * 팀이름을 변경할때, 같은팀이름으로 변경하는지 판단하는 메서드입니다.
     * @param oldTeamName
     * @param newTeamName
     * @return 같은팀이름으로 변경할시, SameTeamNameException 예외를 던집니다.
     */
    public void validateSameTeamName(String oldTeamName, String newTeamName) {
        if ( oldTeamName.equals(newTeamName)) {
            throw new SameTeamNameException("같은 팀이름으로 변경은 불가능합니다.");
        }
    }




}
