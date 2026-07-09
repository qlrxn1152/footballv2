package daehoon.footballv2.member.service.impl;

import daehoon.footballv2.auth.dto.response.signup.SignupResponse;
import daehoon.footballv2.auth.service.AuthService;
import daehoon.footballv2.member.dto.response.MemberRankingResponse;
import daehoon.footballv2.member.service.MemberService;
import daehoon.footballv2.team.dto.response.teamcreate.TeamCreateResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestCreateResponse;
import daehoon.footballv2.team.service.TeamService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberServiceImplTest {

    @Autowired private MemberService memberService;
    @Autowired private AuthService authService;
    @Autowired private TeamService teamService;

    @Test
    @DisplayName(value = "랭킹순으로 멤버조회")
    void findRankingMembers() throws Exception {
        // given
        SignupResponse userA = authService.signup("userA", "1234");
        SignupResponse userB = authService.signup("userB", "1234");
        SignupResponse userC = authService.signup("userC", "1234");

        userA.setMemberRating(1800);
        userB.setMemberRating(1400);
        // rating -> userA, userB, userC => 1800, 1400, 1500

        // when
        List<MemberRankingResponse> members = memberService.membersRanking();

        // members = [userA, userC, userB]

        // then
        assertThat(members.size()).isEqualTo(3);
    }

}