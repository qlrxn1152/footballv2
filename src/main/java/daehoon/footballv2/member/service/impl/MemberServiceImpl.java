package daehoon.footballv2.member.service.impl;

import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.member.dto.response.MemberDetailResponse;
import daehoon.footballv2.member.dto.response.MemberMeResponse;
import daehoon.footballv2.member.dto.response.MemberRankingResponse;
import daehoon.footballv2.member.exception.exceptions.NotFoundMemberException;
import daehoon.footballv2.member.repository.MemberRepository;
import daehoon.footballv2.member.service.MemberService;
import daehoon.footballv2.team.domain.TeamMember;
import daehoon.footballv2.team.repository.TeamMemberRepository;
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

}
