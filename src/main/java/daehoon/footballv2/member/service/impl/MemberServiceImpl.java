package daehoon.footballv2.member.service.impl;

import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.member.dto.response.*;
import daehoon.footballv2.member.exception.exceptions.NotFoundMemberException;
import daehoon.footballv2.member.repository.MemberRepository;
import daehoon.footballv2.member.service.MemberService;
import daehoon.footballv2.team.domain.TeamJoinRequest;
import daehoon.footballv2.team.domain.TeamJoinRequestStatus;
import daehoon.footballv2.team.domain.TeamMember;
import daehoon.footballv2.team.domain.TeamRole;
import daehoon.footballv2.team.exception.exceptions.*;
import daehoon.footballv2.team.repository.TeamJoinRequestRepository;
import daehoon.footballv2.team.repository.TeamMemberRepository;
import daehoon.footballv2.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamJoinRequestRepository teamJoinRequestRepository;

    @Override
    public List<MemberRankingResponse> membersRanking() {
        List<Member> members = memberRepository.findAllByOrderByMemberRatingDesc();
        // members -> 모든 리스트를 읽음. ( 1번 )
        return IntStream.range(0, members.size())
                .mapToObj(index -> {
                    Member member = members.get(index); // index 번호
                    int rank = index + 1; // 0 부터 시작하니까 ..

                    Optional<TeamMember> teamMemberOptional =
                            teamMemberRepository.findByMemberId(member.getId());

                    if (teamMemberOptional.isEmpty()) {
                        return new MemberRankingResponse(
                                rank,
                                member.getId(),
                                member.getUsername(),
                                member.getMemberRating()
                        );
                    }

                    TeamMember teamMember = teamMemberOptional.get();

                    return new MemberRankingResponse(
                            rank,
                            member.getId(),
                            member.getUsername(),
                            member.getMemberRating(),
                            teamMember.getTeam().getId(),
                            teamMember.getTeam().getTeamName()
                    );
                })
                .toList();
    }

    @Override
    public MemberDetailResponse findMemberDetail(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패"));// 멤버 정보만 가지고있음

        Optional<TeamMember> optTeamMember = teamMemberRepository.findByMemberId(memberId);

        if (optTeamMember.isEmpty()) { // 팀에 속해있지 않는 멤버.
            return new MemberDetailResponse(
                    member.getId(),
                    member.getUsername(),
                    member.getMemberRating(),
                    member.getCreatedAt()
            );
        }

        TeamMember teamMember = optTeamMember.get();
        return new MemberDetailResponse(
                member.getId(),
                member.getUsername(),
                member.getMemberRating(),
                teamMember.getTeam().getId(),
                teamMember.getTeam().getTeamName(),
                teamMember.getTeamRole(),
                teamMember.getJoinedAt(),
                member.getCreatedAt()
        );

    }

    @Override
    public MemberMeResponse findMyInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패"));

        Optional<TeamMember> optTeamMember = teamMemberRepository.findByMemberId(memberId);


        if (optTeamMember.isEmpty()) { // 팀에 속해있지 않는 멤버.
            return new MemberMeResponse(
                    member.getId(),
                    member.getUsername(),
                    member.getMemberRating(),
                    member.getCreatedAt()
            );
        }

        TeamMember teamMember = optTeamMember.get();
        return new MemberMeResponse(
                member.getId(),
                member.getUsername(),
                member.getMemberRating(),
                teamMember.getTeam().getId(),
                teamMember.getTeam().getTeamName(),
                teamMember.getTeamRole(),
                teamMember.getJoinedAt(),
                member.getCreatedAt()
        );

    }

    @Override
    public List<MyTeamJoinRequestResponse> findMyTeamJoinRequests(Long memberId, TeamJoinRequestStatus status) {

        return teamJoinRequestRepository.findAllByMemberIdAndStatusOrderByCreatedAtDesc(memberId, status)
                .stream()
                .map(request -> new MyTeamJoinRequestResponse(
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
    public MyTeamJoinRequestResponse cancelRequest(Long joinRequestId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패"));

        TeamJoinRequest joinRequest = teamJoinRequestRepository.findById(joinRequestId)
                .orElseThrow(() -> new NotFoundTeamJoinRequestException("가입신청 조회 실패"));

        if (!member.getId().equals(joinRequest.getMember().getId())) {
            throw new TeamJoinRequestException("회원의 요청이 아닙니다.");
        } // 본인요청인지 확인

        if (joinRequest.getStatus() == TeamJoinRequestStatus.CANCELED) {
            throw new NotPendingException("이미 취소한 요청입니다.");
        }

        if (joinRequest.getStatus() != TeamJoinRequestStatus.PENDING) {
            throw new NotPendingException("이미 승인 / 거절 된 요청입니다.");
        } // status = PENDING 인지 확인

        joinRequest.canceledRequest();
        return new MyTeamJoinRequestResponse(
                joinRequest.getId(),
                joinRequest.getTeam().getId(),
                joinRequest.getTeam().getTeamName(),
                member.getId(),
                member.getUsername(),
                joinRequest.getStatus(),
                joinRequest.getCreatedAt()
        );

    }

    @Override
    public TeamLeaveResponse leaveTeam(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패."));
        TeamMember teamMember = teamMemberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new NotJoinedTeamException("팀에 속해있지 않습니다."));

        if (teamMember.getTeamRole() == TeamRole.LEADER) {
            throw new CannotLeaveTeamLeaderException("팀장은 탈퇴가 불가능합니다.");
        }

        // MEMBER 인 경우에만 실시.
        teamMemberRepository.delete(teamMember);

        return new TeamLeaveResponse(
                member.getId(),
                member.getUsername(),
                teamMember.getTeam().getId(),
                teamMember.getTeam().getTeamName(),
                teamMember.getTeamRole()
        );

    }

}
