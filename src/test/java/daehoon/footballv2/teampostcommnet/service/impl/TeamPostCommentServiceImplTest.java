package daehoon.footballv2.teampostcommnet.service.impl;

import daehoon.footballv2.auth.dto.response.signup.SignupResponse;
import daehoon.footballv2.auth.service.AuthService;
import daehoon.footballv2.team.dto.response.teamcreate.TeamCreateResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestCreateResponse;
import daehoon.footballv2.team.exception.exceptions.NotJoinedTeamException;
import daehoon.footballv2.team.exception.exceptions.NotSameTeamException;
import daehoon.footballv2.team.service.TeamService;
import daehoon.footballv2.teampost.dto.request.TeamPostCreateRequest;
import daehoon.footballv2.teampost.dto.response.TeamPostDetailResponse;
import daehoon.footballv2.teampost.exception.exceptions.NotFoundTeamPostException;
import daehoon.footballv2.teampost.exception.exceptions.NotSameAuthorMemberException;
import daehoon.footballv2.teampost.service.TeamPostService;
import daehoon.footballv2.teampostcommnet.dto.request.TeamPostCommentCreateRequest;
import daehoon.footballv2.teampostcommnet.dto.request.TeamPostCommentUpdateRequest;
import daehoon.footballv2.teampostcommnet.dto.response.TeamPostCommentResponse;
import daehoon.footballv2.teampostcommnet.exception.exceptions.NotFoundTeamPostCommentException;
import daehoon.footballv2.teampostcommnet.service.TeamPostCommentService;
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
class TeamPostCommentServiceImplTest {

    @Autowired private TeamPostCommentService teamPostCommentService;
    @Autowired private TeamPostService teamPostService;
    @Autowired private TeamService teamService;
    @Autowired private AuthService authService;

    @Test
    @DisplayName(value = "팀원 댓글 작성 성공")
    void createTeamPostComment_success() throws Exception {
        // given
        SignupResponse member = authService.signup("memberA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        TeamPostCreateRequest postRequest = new TeamPostCreateRequest("title", "comment");
        TeamPostDetailResponse postResponse = teamPostService.createTeamPost(member.getMemberId(), team.getTeamId(), postRequest);

        TeamPostCommentCreateRequest comment = new TeamPostCommentCreateRequest("comment1");

        // when
        TeamPostCommentResponse response = teamPostCommentService.createTeamPostComment(team.getTeamId(), postResponse.getPostId(), member.getMemberId(), comment);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCommentId()).isNotNull();
        assertThat(response.getPostId()).isEqualTo(postResponse.getPostId());
        assertThat(response.getAuthorMemberId()).isEqualTo(member.getMemberId());
        assertThat(response.getAuthorUsername()).isEqualTo("memberA");
        assertThat(response.getContent()).isEqualTo("comment1");
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(teamPostCommentService.findTeamPostComments(team.getTeamId(), postResponse.getPostId(), member.getMemberId())).hasSize(1);
    }

    @Test
    @DisplayName(value = "외부 회원 댓글 작성 실패")
    void createTeamPostComment_notTeamMember() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamPostDetailResponse postResponse = teamPostService.createTeamPost(memberA.getMemberId(), team.getTeamId(), new TeamPostCreateRequest("title", "comment"));

        // when && then
        assertThatThrownBy(() -> teamPostCommentService.createTeamPostComment(team.getTeamId(), postResponse.getPostId(), memberB.getMemberId(), new TeamPostCommentCreateRequest("comment1")))
                .isInstanceOf(NotJoinedTeamException.class)
                .hasMessage("팀에 속해있지 않는 멤버입니다.");
    }

    @Test
    @DisplayName(value = "외부 팀 회원 댓글 작성 실패")
    void createTeamPostComment_wrongTeam() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());
        TeamPostDetailResponse postResponse = teamPostService.createTeamPost(memberA.getMemberId(), team.getTeamId(), new TeamPostCreateRequest("title", "comment"));

        // when && then
        assertThatThrownBy(() -> teamPostCommentService.createTeamPostComment(team.getTeamId(), postResponse.getPostId(), memberB.getMemberId(), new TeamPostCommentCreateRequest("comment1")))
                .isInstanceOf(NotSameTeamException.class)
                .hasMessage("다른팀 소속입니다.");
    }

    @Test
    @DisplayName(value = "댓글 목록 조회 성공")
    void findTeamPostComments_success() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamPostDetailResponse post1 = teamPostService.createTeamPost(memberA.getMemberId(), team.getTeamId(), new TeamPostCreateRequest("title", "comment"));
        TeamPostDetailResponse post2 = teamPostService.createTeamPost(memberA.getMemberId(), team.getTeamId(), new TeamPostCreateRequest("title2", "comment2"));

        teamPostCommentService.createTeamPostComment(team.getTeamId(), post1.getPostId(), memberA.getMemberId(), new TeamPostCommentCreateRequest("comment1"));
        teamPostCommentService.createTeamPostComment(team.getTeamId(), post1.getPostId(), memberA.getMemberId(), new TeamPostCommentCreateRequest("comment2"));
        teamPostCommentService.createTeamPostComment(team.getTeamId(), post2.getPostId(), memberA.getMemberId(), new TeamPostCommentCreateRequest("comment3"));
        // when

        List<TeamPostCommentResponse> post1Response = teamPostCommentService.findTeamPostComments(team.getTeamId(), post1.getPostId(), memberA.getMemberId());
        List<TeamPostCommentResponse> post2Response = teamPostCommentService.findTeamPostComments(team.getTeamId(), post2.getPostId(), memberA.getMemberId());

        // then
        assertThat(post1Response).isNotNull();
        assertThat(post2Response).isNotNull();
        assertThat(post1Response).hasSize(2);
        assertThat(post2Response).hasSize(1);
    }

    @Test
    @DisplayName(value = "댓글없으면 빈 목록")
    void findTeamPostComments_empty() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamPostDetailResponse post1 = teamPostService.createTeamPost(memberA.getMemberId(), team.getTeamId(), new TeamPostCreateRequest("title", "comment"));

        // when
        List<TeamPostCommentResponse> post1Response = teamPostCommentService.findTeamPostComments(team.getTeamId(), post1.getPostId(), memberA.getMemberId());

        // then
        assertThat(post1Response).isNotNull();
        assertThat(post1Response).isEmpty();
    }

    @Test
    @DisplayName(value = "외부회원 댓글 목록 조회 실패")
    void findTeamPostComments_notTeamMember() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamPostDetailResponse post1 = teamPostService.createTeamPost(memberA.getMemberId(), team.getTeamId(), new TeamPostCreateRequest("title", "comment"));

        // when && then
        assertThatThrownBy(() -> teamPostCommentService.findTeamPostComments(team.getTeamId(), post1.getPostId(), memberB.getMemberId()))
                .isInstanceOf(NotJoinedTeamException.class)
                .hasMessage("팀에 속해있지 않는 멤버입니다.");
    }

    @Test
    @DisplayName(value = "외부 팀 회원 댓글 목록 조회 실패")
    void findTeamPostComments_notTeamMember2() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamPostDetailResponse post1 = teamPostService.createTeamPost(memberA.getMemberId(), team.getTeamId(), new TeamPostCreateRequest("title", "comment"));

        // when && then
        assertThatThrownBy(() -> teamPostCommentService.findTeamPostComments(team.getTeamId(), post1.getPostId(), memberB.getMemberId()))
                .isInstanceOf(NotJoinedTeamException.class)
                .hasMessage("팀에 속해있지 않는 멤버입니다.");
    }

    @Test
    @DisplayName(value = "외부 팀 회원 댓글 목록 조회 실패")
    void findTeamPostComments_notTeamMember3() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        teamService.createTeam("teamB", memberB.getMemberId());
        TeamPostDetailResponse post1 = teamPostService.createTeamPost(memberA.getMemberId(), team.getTeamId(), new TeamPostCreateRequest("title", "comment"));

        // when && then
        assertThatThrownBy(() -> teamPostCommentService.findTeamPostComments(team.getTeamId(), post1.getPostId(), memberB.getMemberId()))
                .isInstanceOf(NotSameTeamException.class)
                .hasMessage("다른팀 소속입니다.");
    }

    @Test
    @DisplayName(value = "존재하지 않는 게시물에 댓글 작성 실패")
    void findTeamPostComments_notTeamMember4() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());

        // when && then
        assertThatThrownBy(() -> teamPostCommentService.findTeamPostComments(team.getTeamId(), 999L, memberA.getMemberId()))
                .isInstanceOf(NotFoundTeamPostException.class)
                .hasMessage("팀 포스트 조회 실패");
    }

    @Test
    @DisplayName(value = "작성자 수정 성공")
    void updateTeamPostComment_success() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());

        TeamPostDetailResponse postResponse = teamPostService.createTeamPost(memberA.getMemberId(), team.getTeamId(), new TeamPostCreateRequest("title", "comment"));
        TeamPostCommentResponse commentResponse = teamPostCommentService.createTeamPostComment(team.getTeamId(), postResponse.getPostId(), memberA.getMemberId(), new TeamPostCommentCreateRequest("comment"));

        // when
        TeamPostCommentResponse response = teamPostCommentService.updateTeamPostComment(memberA.getMemberId(), team.getTeamId(), postResponse.getPostId(), commentResponse.getCommentId(), new TeamPostCommentUpdateRequest("newComment"));
        List<TeamPostCommentResponse> teamPostComments = teamPostCommentService.findTeamPostComments(team.getTeamId(), postResponse.getPostId(), memberA.getMemberId());

        // then
        assertThat(teamPostComments).hasSize(1);
        assertThat(teamPostComments.get(0).getContent()).isEqualTo("newComment");
    }

    @Test
    @DisplayName(value = "같은 팀 다른회원")
    void updateTeamPostComment_notAuthor() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());

        TeamJoinRequestCreateResponse joinRequest = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());
        teamService.acceptRequest(joinRequest.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId());

        TeamPostDetailResponse postResponse = teamPostService.createTeamPost(memberA.getMemberId(), team.getTeamId(), new TeamPostCreateRequest("title", "comment"));
        TeamPostCommentResponse commentResponse = teamPostCommentService.createTeamPostComment(team.getTeamId(), postResponse.getPostId(), memberA.getMemberId(), new TeamPostCommentCreateRequest("comment"));

        // when && then
        assertThatThrownBy(() -> teamPostCommentService.updateTeamPostComment(memberB.getMemberId(), team.getTeamId(), postResponse.getPostId(), commentResponse.getCommentId(), new TeamPostCommentUpdateRequest("newComment")))
                .isInstanceOf(NotSameAuthorMemberException.class)
                .hasMessage("해당 댓글 작성자가 아닙니다.");
    }

    @Test
    @DisplayName(value = "다른 팀 회원")
    void updateTeamPostComment_notTeamMember() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamPostDetailResponse postResponse = teamPostService.createTeamPost(memberA.getMemberId(), team.getTeamId(), new TeamPostCreateRequest("title", "comment"));
        TeamPostCommentResponse commentResponse = teamPostCommentService.createTeamPostComment(team.getTeamId(), postResponse.getPostId(), memberA.getMemberId(), new TeamPostCommentCreateRequest("comment"));

        // when && then
        assertThatThrownBy(() -> teamPostCommentService.updateTeamPostComment(memberB.getMemberId(), team.getTeamId(), postResponse.getPostId(), commentResponse.getCommentId(), new TeamPostCommentUpdateRequest("newComment")))
                .isInstanceOf(NotSameTeamException.class)
                .hasMessage("다른팀 소속입니다.");
    }

    @Test
    @DisplayName(value = "존재하지 않는 게시글")
    void updateTeamPostComment_wrongPost() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamPostDetailResponse postResponse = teamPostService.createTeamPost(memberA.getMemberId(), team.getTeamId(), new TeamPostCreateRequest("title", "comment"));
        TeamPostCommentResponse commentResponse = teamPostCommentService.createTeamPostComment(team.getTeamId(), postResponse.getPostId(), memberA.getMemberId(), new TeamPostCommentCreateRequest("comment"));

        // when && then
        assertThatThrownBy(() -> teamPostCommentService.updateTeamPostComment(memberA.getMemberId(), team.getTeamId(), 999L, commentResponse.getCommentId(), new TeamPostCommentUpdateRequest("newComment")))
                .isInstanceOf(NotFoundTeamPostException.class)
                .hasMessage("팀 포스트 조회 실패");
    }

    @Test
    @DisplayName(value = "존재하지 않는 댓글")
    void updateTeamPostComment_wrongPost2() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());

        TeamPostDetailResponse postResponse = teamPostService.createTeamPost(memberA.getMemberId(), team.getTeamId(), new TeamPostCreateRequest("title", "comment"));
        TeamPostCommentResponse commentResponse = teamPostCommentService.createTeamPostComment(team.getTeamId(), postResponse.getPostId(), memberA.getMemberId(), new TeamPostCommentCreateRequest("comment"));

        // when && then
        assertThatThrownBy(() -> teamPostCommentService.updateTeamPostComment(memberA.getMemberId(), team.getTeamId(), postResponse.getPostId(), 999L, new TeamPostCommentUpdateRequest("newComment")))
                .isInstanceOf(NotFoundTeamPostCommentException.class)
                .hasMessage("댓글 조회 실패");
    }

    @Test
    @DisplayName(value = "댓글 삭제 성공")
    void deleteTeamPostComment_success() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());

        TeamPostDetailResponse postResponse = teamPostService.createTeamPost(memberA.getMemberId(), team.getTeamId(), new TeamPostCreateRequest("title", "comment"));
        TeamPostCommentResponse commentResponse = teamPostCommentService.createTeamPostComment(team.getTeamId(), postResponse.getPostId(), memberA.getMemberId(), new TeamPostCommentCreateRequest("comment"));

        // when
        teamPostCommentService.deleteTeamPostComment(memberA.getMemberId(), team.getTeamId(), postResponse.getPostId(), commentResponse.getCommentId());

        List<TeamPostCommentResponse> teamPostComments = teamPostCommentService.findTeamPostComments(team.getTeamId(), postResponse.getPostId(), memberA.getMemberId());

        // then
        assertThat(teamPostComments).isEmpty();
    }

    @Test
    @DisplayName(value = "같은 팀 다른 회원 삭제 실패")
    void deleteTeamPostComment_notAuthor() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());

        TeamJoinRequestCreateResponse joinRequest = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());
        teamService.acceptRequest(joinRequest.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId());

        TeamPostDetailResponse postResponse = teamPostService.createTeamPost(memberA.getMemberId(), team.getTeamId(), new TeamPostCreateRequest("title", "comment"));
        TeamPostCommentResponse commentResponse = teamPostCommentService.createTeamPostComment(team.getTeamId(), postResponse.getPostId(), memberA.getMemberId(), new TeamPostCommentCreateRequest("comment"));

        // when && then
        assertThatThrownBy(() -> teamPostCommentService.deleteTeamPostComment(memberB.getMemberId(), team.getTeamId(), postResponse.getPostId(), commentResponse.getCommentId()))
                .isInstanceOf(NotSameAuthorMemberException.class)
                .hasMessage("해당 댓글 작성자가 아닙니다.");
    }

    @Test
    @DisplayName(value = "같은 팀 다른 회원 삭제 실패")
    void deleteTeamPostComment_notFound() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());

        TeamPostDetailResponse postResponse = teamPostService.createTeamPost(memberA.getMemberId(), team.getTeamId(), new TeamPostCreateRequest("title", "comment"));
        TeamPostCommentResponse commentResponse = teamPostCommentService.createTeamPostComment(team.getTeamId(), postResponse.getPostId(), memberA.getMemberId(), new TeamPostCommentCreateRequest("comment"));

        // when && then
        assertThatThrownBy(() -> teamPostCommentService.deleteTeamPostComment(memberA.getMemberId(), team.getTeamId(), postResponse.getPostId(), 9099L))
                .isInstanceOf(NotFoundTeamPostCommentException.class)
                .hasMessage("댓글 조회 실패");
    }




}