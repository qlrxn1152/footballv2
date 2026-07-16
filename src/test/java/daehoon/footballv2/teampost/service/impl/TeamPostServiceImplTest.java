package daehoon.footballv2.teampost.service.impl;

import daehoon.footballv2.auth.dto.response.signup.SignupResponse;
import daehoon.footballv2.auth.service.AuthService;
import daehoon.footballv2.team.dto.response.teamcreate.TeamCreateResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestCreateResponse;
import daehoon.footballv2.team.exception.exceptions.NotJoinedTeamException;
import daehoon.footballv2.team.exception.exceptions.NotSameTeamException;
import daehoon.footballv2.team.service.TeamService;
import daehoon.footballv2.teampost.domain.TeamPost;
import daehoon.footballv2.teampost.dto.request.TeamPostCreateRequest;
import daehoon.footballv2.teampost.dto.request.TeamPostUpdateRequest;
import daehoon.footballv2.teampost.dto.response.TeamPostDetailResponse;
import daehoon.footballv2.teampost.dto.response.TeamPostSummaryResponse;
import daehoon.footballv2.teampost.exception.exceptions.NotFoundTeamPostException;
import daehoon.footballv2.teampost.exception.exceptions.NotSameAuthorMemberException;
import daehoon.footballv2.teampost.repository.TeamPostRepository;
import daehoon.footballv2.teampost.service.TeamPostService;
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
class TeamPostServiceImplTest {

    @Autowired TeamPostRepository teamPostRepository;
    @Autowired TeamPostService teamPostService;
    @Autowired AuthService authService;
    @Autowired TeamService teamService;

    @Test
    @DisplayName(value = "팀 게시물 작성 성공")
    void createTeamPost_success() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());

        TeamPostCreateRequest postRequest = new TeamPostCreateRequest("title1", "content1");

        // when
        TeamPostDetailResponse response = teamPostService.createTeamPost(member.getMemberId(), team.getTeamId(), postRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPostId()).isNotNull();
        assertThat(response.getTeamId()).isEqualTo(team.getTeamId());
        assertThat(response.getTitle()).isEqualTo("title1");
        assertThat(response.getContent()).isEqualTo("content1");
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();
        assertThat(teamPostRepository.countByTeamId(team.getTeamId())).isEqualTo(1);
    }


    @Test
    @DisplayName(value = "팀에 속하지 않으면 작성 실패")
    void createTeamPost_notTeamMember() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        SignupResponse memberA = authService.signup("memberA", "1234");

        TeamPostCreateRequest postRequest = new TeamPostCreateRequest("title1", "content1");

        // when && then
        assertThatThrownBy(() -> teamPostService.createTeamPost(memberA.getMemberId(), team.getTeamId(), postRequest))
                .isInstanceOf(NotJoinedTeamException.class)
                .hasMessage("팀에 속해있지 않는 멤버입니다.");
    }

    @Test
    @DisplayName(value = "같은팀이 아닌경우는 작성실패")
    void createTeamPost_notSameTeamMember() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        SignupResponse memberA = authService.signup("memberA", "1234");
        teamService.createTeam("teamB", memberA.getMemberId());

        TeamPostCreateRequest postRequest = new TeamPostCreateRequest("title1", "content1");

        // when && then
        assertThatThrownBy(() -> teamPostService.createTeamPost(memberA.getMemberId(), team.getTeamId(), postRequest))
                .isInstanceOf(NotSameTeamException.class)
                .hasMessage("다른팀 소속입니다.");
    }

    @Test
    @DisplayName(value = "팀 게시글 전체 조회 성공")
    void findTeamPosts_success() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());

        TeamPostCreateRequest postRequest = new TeamPostCreateRequest("title1", "content1");
        TeamPostCreateRequest postRequest2 = new TeamPostCreateRequest("title2", "content2");

        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamPostCreateRequest teambPost = new TeamPostCreateRequest("teamB", "1234");

        // when
        teamPostService.createTeamPost(member.getMemberId(), team.getTeamId(), postRequest);
        teamPostService.createTeamPost(member.getMemberId(), team.getTeamId(), postRequest2);
        teamPostService.createTeamPost(memberB.getMemberId(), teamB.getTeamId(), teambPost);

        List<TeamPostSummaryResponse> response = teamPostService.findTeamPosts(member.getMemberId(), team.getTeamId());

        // then
        assertThat(response).hasSize(2);
        assertThat(response).allMatch(post -> post.getPostId() != null);
        assertThat(teamPostRepository.countByTeamId(team.getTeamId())).isEqualTo(2);
    }

    @Test
    @DisplayName(value = "팀 게시글 존재안함")
    void findTeamPosts_success2() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());

        // when
        List<TeamPostSummaryResponse> response = teamPostService.findTeamPosts(member.getMemberId(), team.getTeamId());

        // then
        assertThat(response).isEmpty();
        assertThat(teamPostRepository.countByTeamId(team.getTeamId())).isZero();
    }

    @Test
    @DisplayName(value = "해당팀 아닌 사람이 전체조회")
    void findTeamPosts_notTeamMember() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());

        TeamPostCreateRequest postRequest = new TeamPostCreateRequest("title1", "content1");
        TeamPostCreateRequest postRequest2 = new TeamPostCreateRequest("title2", "content2");

        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        teamPostService.createTeamPost(member.getMemberId(), team.getTeamId(), postRequest);
        teamPostService.createTeamPost(member.getMemberId(), team.getTeamId(), postRequest2);

        // when && then

        assertThatThrownBy(() -> teamPostService.findTeamPosts(memberB.getMemberId(), team.getTeamId()))
                .isInstanceOf(NotSameTeamException.class)
                .hasMessage("다른팀 소속입니다.");
    }

    @Test
    @DisplayName(value = "게시물 상세 조회")
    void findTeamPostDetail_success() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());

        TeamPostCreateRequest postRequest = new TeamPostCreateRequest("title1", "content1");
        TeamPostDetailResponse post = teamPostService.createTeamPost(member.getMemberId(), team.getTeamId(), postRequest); // teamA 에 포스트 작성

        // when
        TeamPostDetailResponse response = teamPostService.findTeamPost(member.getMemberId(), team.getTeamId(), post.getPostId());

        // then
        assertThat(response.getPostId()).isEqualTo(post.getPostId());
        assertThat(response.getTeamId()).isEqualTo(team.getTeamId());
        assertThat(response.getTitle()).isEqualTo("title1");
        assertThat(response.getContent()).isEqualTo("content1");
        assertThat(response.getAuthorUsername()).isEqualTo("test");
        assertThat(response.getAuthorMemberId()).isEqualTo(member.getMemberId());
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName(value = "다른팀 아이디로 게시글 조회")
    void findTeamPostDetail_wrongTeam() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        TeamPostCreateRequest postRequest = new TeamPostCreateRequest("title1", "content1");
        TeamPostDetailResponse post = teamPostService.createTeamPost(member.getMemberId(), team.getTeamId(), postRequest);

        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        // when && then
        assertThatThrownBy(() -> teamPostService.findTeamPost(memberB.getMemberId(), teamB.getTeamId(), post.getPostId()))
                .isInstanceOf(NotFoundTeamPostException.class)
                .hasMessage("팀 포스트 조회 실패");
    }

    @Test
    @DisplayName(value = "존재하지 않는 게시글 조회 실패")
    void findTeamPostDetail_notFound() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        TeamPostCreateRequest postRequest = new TeamPostCreateRequest("title1", "content1");
        teamPostService.createTeamPost(member.getMemberId(), team.getTeamId(), postRequest);

        // when && then
        assertThatThrownBy(() -> teamPostService.findTeamPost(member.getMemberId(), team.getTeamId(), 999L))
                .isInstanceOf(NotFoundTeamPostException.class)
                .hasMessage("팀 포스트 조회 실패");
    }

    @Test
    @DisplayName(value = "게시물 수정")
    void updateTeamPost_success() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());

        TeamPostCreateRequest postRequest = new TeamPostCreateRequest("title1", "content1");
        TeamPostDetailResponse post = teamPostService.createTeamPost(member.getMemberId(), team.getTeamId(), postRequest); // teamA 에 포스트 작성

        TeamPostUpdateRequest updateRequest = new TeamPostUpdateRequest("newTitle", "newContent");

        // when
        TeamPostDetailResponse response = teamPostService.updateTeamPost(member.getMemberId(), team.getTeamId(), post.getPostId(), updateRequest);

        // then
        assertThat(response.getPostId()).isEqualTo(post.getPostId());
        assertThat(response.getTeamId()).isEqualTo(team.getTeamId());
        assertThat(response.getTitle()).isEqualTo("newTitle");
        assertThat(response.getContent()).isEqualTo("newContent");
        assertThat(response.getAuthorUsername()).isEqualTo("test");
        assertThat(response.getAuthorMemberId()).isEqualTo(member.getMemberId());
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotEqualTo(post.getUpdatedAt());
    }

    @Test
    @DisplayName(value = "다른회원 게시물 수정 실패")
    void updateTeamPost_notAuthor() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        TeamJoinRequestCreateResponse joinRequest = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());
        teamService.acceptRequest(joinRequest.getTeamJoinRequestId(), team.getTeamId(), member.getMemberId());

        TeamPostCreateRequest postRequest = new TeamPostCreateRequest("title1", "content1");
        TeamPostDetailResponse post = teamPostService.createTeamPost(member.getMemberId(), team.getTeamId(), postRequest); // teamA 에 포스트 작성

        TeamPostUpdateRequest updateRequest = new TeamPostUpdateRequest("newTitle", "newContent");

        // when && then
        assertThatThrownBy(() -> teamPostService.updateTeamPost(memberB.getMemberId(), team.getTeamId(), post.getPostId(), updateRequest))
                .isInstanceOf(NotSameAuthorMemberException.class)
                .hasMessage("해당 글 작성자가 아닙니다.");
    }

    @Test
    @DisplayName(value = "다른 팀 게시물 수정 실패")
    void updateTeamPost_notSameTeam() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());

        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse taemB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamPostCreateRequest postRequest = new TeamPostCreateRequest("title1", "content1");
        TeamPostDetailResponse post = teamPostService.createTeamPost(member.getMemberId(), team.getTeamId(), postRequest); // teamA 에 포스트 작성

        TeamPostUpdateRequest updateRequest = new TeamPostUpdateRequest("newTitle", "newContent");

        // when && then
        assertThatThrownBy(() -> teamPostService.updateTeamPost(memberB.getMemberId(), team.getTeamId(), post.getPostId(), updateRequest))
                .isInstanceOf(NotSameTeamException.class)
                .hasMessage("다른팀 소속입니다.");
    }

    @Test
    @DisplayName(value = "다른 팀 게시물 수정 실패")
    void updateTeamPost_wrongTeam() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());

        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamPostCreateRequest postRequest = new TeamPostCreateRequest("title1", "content1");
        TeamPostDetailResponse post = teamPostService.createTeamPost(member.getMemberId(), team.getTeamId(), postRequest); // teamA 에 포스트 작성

        TeamPostUpdateRequest updateRequest = new TeamPostUpdateRequest("newTitle", "newContent");

        // when && then
        assertThatThrownBy(() -> teamPostService.updateTeamPost(memberB.getMemberId(), teamB.getTeamId(), post.getPostId(), updateRequest))
                .isInstanceOf(NotFoundTeamPostException.class)
                .hasMessage("팀 포스트 조회 실패");
    }

    @Test
    @DisplayName(value = "존재하지 않는 게시물")
    void updateTeamPost_notFound() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());

        TeamPostCreateRequest postRequest = new TeamPostCreateRequest("title1", "content1");
        TeamPostDetailResponse post = teamPostService.createTeamPost(member.getMemberId(), team.getTeamId(), postRequest); // teamA 에 포스트 작성

        TeamPostUpdateRequest updateRequest = new TeamPostUpdateRequest("newTitle", "newContent");

        // when && then
        assertThatThrownBy(() -> teamPostService.updateTeamPost(member.getMemberId(), team.getTeamId(), 9999L, updateRequest))
                .isInstanceOf(NotFoundTeamPostException.class)
                .hasMessage("팀 포스트 조회 실패");
    }

    @Test
    @DisplayName(value = "작성자 삭제 성공")
    void deleteTeamPost_success() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());

        TeamPostCreateRequest postRequest = new TeamPostCreateRequest("title1", "content1");
        TeamPostDetailResponse post = teamPostService.createTeamPost(member.getMemberId(), team.getTeamId(), postRequest); // teamA 에 포스트 작성

        // when
        teamPostService.deleteTeamPost(member.getMemberId(), team.getTeamId(), post.getPostId());

        List<TeamPostSummaryResponse> posts = teamPostService.findTeamPosts(member.getMemberId(), post.getTeamId());

        // then
        assertThat(posts).isEmpty();
    }

    @Test
    @DisplayName(value = "같은팀 다른회원 삭제 실패 ")
    void deleteTeamPost_notAuthor() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");

        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        TeamJoinRequestCreateResponse joinRequest = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());
        teamService.acceptRequest(joinRequest.getTeamJoinRequestId(), team.getTeamId(), member.getMemberId());

        TeamPostCreateRequest postRequest = new TeamPostCreateRequest("title1", "content1");
        TeamPostDetailResponse post = teamPostService.createTeamPost(member.getMemberId(), team.getTeamId(), postRequest); // teamA 에 포스트 작성

        // when && then
        assertThatThrownBy(() -> teamPostService.deleteTeamPost(memberB.getMemberId(), team.getTeamId(), post.getPostId()))
                .isInstanceOf(NotSameAuthorMemberException.class)
                .hasMessage("해당 글 작성자가 아닙니다.");
    }

    @Test
    @DisplayName(value = "다른팀 회원의 게시물 삭제 실패 ")
    void deleteTeamPost_notTeamMember() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");

        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamPostCreateRequest postRequest = new TeamPostCreateRequest("title1", "content1");
        TeamPostDetailResponse post = teamPostService.createTeamPost(member.getMemberId(), team.getTeamId(), postRequest); // teamA 에 포스트 작성

        // when && then
        assertThatThrownBy(() -> teamPostService.deleteTeamPost(memberB.getMemberId(), team.getTeamId(), post.getPostId()))
                .isInstanceOf(NotSameTeamException.class)
                .hasMessage("다른팀 소속입니다.");
    }

    @Test
    @DisplayName(value = "존재하지 않는 게시물 삭제 실패")
    void deleteTeamPost_notFound() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());

        TeamPostCreateRequest postRequest = new TeamPostCreateRequest("title1", "content1");
        TeamPostDetailResponse post = teamPostService.createTeamPost(member.getMemberId(), team.getTeamId(), postRequest); // teamA 에 포스트 작성

        // when && then
        assertThatThrownBy(() -> teamPostService.deleteTeamPost(member.getMemberId(), team.getTeamId(), 8888L))
                .isInstanceOf(NotFoundTeamPostException.class)
                .hasMessage("팀 포스트 조회 실패");
    }



}