package daehoon.footballv2.member.service.impl;

import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.member.dto.response.MemberRankingResponse;
import daehoon.footballv2.member.exception.NotFoundMemberException;
import daehoon.footballv2.member.repository.MemberRepository;
import daehoon.footballv2.member.service.MemberService;
import daehoon.footballv2.team.domain.TeamMember;
import daehoon.footballv2.team.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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

}
