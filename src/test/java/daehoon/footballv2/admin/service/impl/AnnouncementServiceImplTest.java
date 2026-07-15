package daehoon.footballv2.admin.service.impl;

import daehoon.footballv2.admin.domain.AnnouncementType;
import daehoon.footballv2.admin.dto.request.AnnouncementCreateRequest;
import daehoon.footballv2.admin.dto.request.AnnouncementUpdateRequest;
import daehoon.footballv2.admin.dto.response.AnnouncementDetailResponse;
import daehoon.footballv2.admin.dto.response.AnnouncementSummaryResponse;
import daehoon.footballv2.admin.exception.exceptions.NotAdminException;
import daehoon.footballv2.admin.exception.exceptions.NotFoundAnnouncementException;
import daehoon.footballv2.admin.service.AnnouncementService;
import daehoon.footballv2.auth.dto.response.signup.SignupResponse;
import daehoon.footballv2.auth.service.AuthService;
import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.member.domain.MemberAuthority;
import daehoon.footballv2.member.exception.exceptions.NotFoundMemberException;
import daehoon.footballv2.member.service.MemberService;
import daehoon.footballv2.team.validator.TeamValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AnnouncementServiceImplTest {

    @Autowired AnnouncementService announcementService;
    @Autowired AuthService authService;
    @Autowired TeamValidator teamValidator;


    @Test
    @DisplayName(value = "공지사항 생성")
    void create_announcement() throws Exception {
        // given
        Member member = teamValidator.validateMemberExists(authService.signup("userA", "1234").getMemberId());
        member.changeAuthority(MemberAuthority.ADMIN);

        AnnouncementCreateRequest request = new AnnouncementCreateRequest(AnnouncementType.NOTICE, "testTitle", "testContent", "0.0.1", false);

        // when
        AnnouncementDetailResponse response = announcementService.createAnnouncement(member.getId(), request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAnnouncementType()).isEqualTo(AnnouncementType.NOTICE);
        assertThat(response.getTitle()).isEqualTo("testTitle");
        assertThat(response.getContent()).isEqualTo("testContent");
        assertThat(response.getVersion()).isEqualTo("0.0.1");
    }

    @Test
    @DisplayName(value = "어드민이 아닌자는 공지사항 생성 실패")
    void create_announcement_not_admin() throws Exception {
        // given
        SignupResponse member = authService.signup("userA", "1234");

        AnnouncementCreateRequest request = new AnnouncementCreateRequest(AnnouncementType.NOTICE, "testTitle", "testContent", "0.0.1", false);

        // when && then
        assertThatThrownBy(() -> announcementService.createAnnouncement(member.getMemberId(), request))
                .isInstanceOf(NotAdminException.class)
                .hasMessage("관리자 권한이 없습니다.");
    }

    @Test
    @DisplayName(value = "공지사항 전체 조회 성공")
    void findAnnouncements() throws Exception {
        // given
        Member member = teamValidator.validateMemberExists(authService.signup("userA", "1234").getMemberId());
        member.changeAuthority(MemberAuthority.ADMIN);

        AnnouncementCreateRequest request = new AnnouncementCreateRequest(AnnouncementType.NOTICE, "testTitle", "testContent", "0.0.1", false);
        AnnouncementDetailResponse response = announcementService.createAnnouncement(member.getId(), request);

        // when
        List<AnnouncementSummaryResponse> announcements = announcementService.findAnnouncements();


        // then
        assertThat(announcements).hasSize(1);
        AnnouncementSummaryResponse announcement = announcements.get(0);
        assertThat(announcement.getTitle()).isEqualTo("testTitle");
    }

    @Test
    @DisplayName(value = "공지사항 특정 게시물 조회 성공")
    void findAnnouncement() throws Exception {
        // given
        Member member = teamValidator.validateMemberExists(authService.signup("userA", "1234").getMemberId());
        member.changeAuthority(MemberAuthority.ADMIN);

        AnnouncementCreateRequest request = new AnnouncementCreateRequest(AnnouncementType.NOTICE, "testTitle", "testContent", "0.0.1", false);
        AnnouncementDetailResponse response = announcementService.createAnnouncement(member.getId(), request);

        // when
        AnnouncementDetailResponse announcement = announcementService.findAnnouncementDetail(response.getId());

        // then
        assertThat(announcement.getId()).isEqualTo(response.getId());
    }

    @Test
    @DisplayName(value = "공지 수정 성공")
    void updateAnnouncement() throws Exception {
        // given
        Member member = teamValidator.validateMemberExists(authService.signup("userA", "1234").getMemberId());
        member.changeAuthority(MemberAuthority.ADMIN);

        AnnouncementCreateRequest request = new AnnouncementCreateRequest(AnnouncementType.NOTICE, "testTitle", "testContent", "0.0.1", false);
        AnnouncementDetailResponse announcement = announcementService.createAnnouncement(member.getId(), request);

        AnnouncementUpdateRequest updateRequest = new AnnouncementUpdateRequest(AnnouncementType.NOTICE, "title", "content", "0.0.1", false);

        // when
        AnnouncementDetailResponse response = announcementService.updateAnnouncement(member.getId(), announcement.getId(), updateRequest);

        // then
        assertThat(response.getId()).isEqualTo(announcement.getId());
        assertThat(response.getTitle()).isEqualTo("title");
        assertThat(response.getContent()).isEqualTo("content");
        assertThat(response.getVersion()).isEqualTo("0.0.1");
    }

    @Test
    @DisplayName(value = "공지 수정 실패 ( 권한없음 ) ")
    void updateAnnouncement_USER() throws Exception {
        // given
        Member member = teamValidator.validateMemberExists(authService.signup("userA", "1234").getMemberId());
        member.changeAuthority(MemberAuthority.ADMIN);

        SignupResponse notAdminMember = authService.signup("userB", "1234");

        AnnouncementCreateRequest request = new AnnouncementCreateRequest(AnnouncementType.NOTICE, "testTitle", "testContent", "0.0.1", false);
        AnnouncementDetailResponse announcement = announcementService.createAnnouncement(member.getId(), request);

        AnnouncementUpdateRequest updateRequest = new AnnouncementUpdateRequest(AnnouncementType.NOTICE, "title", "content", "0.0.1", false);

        // when
        assertThatThrownBy(() -> announcementService.updateAnnouncement(notAdminMember.getMemberId(), announcement.getId(), updateRequest))
                .isInstanceOf(NotAdminException.class)
                .hasMessage("관리자 권한이 없습니다.");
    }

    @Test
    @DisplayName(value = "공지 수정 실패 ( 멤버조회 실패 ) ")
    void updateAnnouncement_notFoundMember() throws Exception {
        // given
        Member member = teamValidator.validateMemberExists(authService.signup("userA", "1234").getMemberId());
        member.changeAuthority(MemberAuthority.ADMIN);

        AnnouncementCreateRequest request = new AnnouncementCreateRequest(AnnouncementType.NOTICE, "testTitle", "testContent", "0.0.1", false);
        AnnouncementDetailResponse announcement = announcementService.createAnnouncement(member.getId(), request);

        AnnouncementUpdateRequest updateRequest = new AnnouncementUpdateRequest(AnnouncementType.NOTICE, "title", "content", "0.0.1", false);

        // when
        assertThatThrownBy(() -> announcementService.updateAnnouncement(9999L, announcement.getId(), updateRequest))
                .isInstanceOf(NotFoundMemberException.class)
                .hasMessage("멤버 조회 실패.");
    }

    @Test
    @DisplayName(value = "공지 삭제 성공 ")
    void deleteAnnouncement() throws Exception {
        // given
        Member member = teamValidator.validateMemberExists(authService.signup("userA", "1234").getMemberId());
        member.changeAuthority(MemberAuthority.ADMIN);

        AnnouncementCreateRequest request = new AnnouncementCreateRequest(AnnouncementType.NOTICE, "testTitle", "testContent", "0.0.1", false);
        AnnouncementDetailResponse announcement = announcementService.createAnnouncement(member.getId(), request);

        // when
        announcementService.deleteAnnouncement(member.getId(), announcement.getId());

        // then
        assertThatThrownBy(() -> announcementService.findAnnouncementDetail(announcement.getId()))
                .isInstanceOf(NotFoundAnnouncementException.class)
                .hasMessage("공지사항 게시물을 조회하지 못했습니다.");
    }

    @Test
    @DisplayName(value = "공지 삭제 실패 _ 권한없음 ")
    void deleteAnnouncement_USER() throws Exception {
        // given
        Member member = teamValidator.validateMemberExists(authService.signup("userA", "1234").getMemberId());
        member.changeAuthority(MemberAuthority.ADMIN);

        SignupResponse notAdminMember = authService.signup("userB", "1234");

        AnnouncementCreateRequest request = new AnnouncementCreateRequest(AnnouncementType.NOTICE, "testTitle", "testContent", "0.0.1", false);
        AnnouncementDetailResponse announcement = announcementService.createAnnouncement(member.getId(), request);

        // when && then
        assertThatThrownBy(() -> announcementService.deleteAnnouncement(notAdminMember.getMemberId(), announcement.getId()))
                .isInstanceOf(NotAdminException.class)
                .hasMessage("관리자 권한이 없습니다.");

    }









}