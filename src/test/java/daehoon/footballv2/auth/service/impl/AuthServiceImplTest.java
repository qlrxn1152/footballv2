package daehoon.footballv2.auth.service.impl;

import daehoon.footballv2.auth.dto.login.LoginResponse;
import daehoon.footballv2.auth.dto.signup.SignupResponse;
import daehoon.footballv2.auth.exception.exceptions.DuplicateUsernameException;
import daehoon.footballv2.auth.exception.exceptions.InvalidLoginException;
import daehoon.footballv2.auth.service.AuthService;
import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceImplTest {

    @Autowired private AuthService authService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName(value = "회원가입 성공")
    void signup() throws Exception {
        // given
        String username = "memberA";
        String password = "1234";

        // when
        SignupResponse response = authService.signup(username, password);
        Member member = memberRepository.findByUsername(username).get();

        // then
        assertThat(member.getUsername()).isEqualTo(username);
        assertThat(member.getRating()).isEqualTo(1500);
        assertThat(member.getId()).isNotNull();

        assertThat(passwordEncoder.matches(password, member.getPassword())).isEqualTo(true);
        assertThat(member.getPassword()).isNotEqualTo(password);

        assertThat(response.getUsername()).isEqualTo(username);
        assertThat(response.getMemberRating()).isEqualTo(1500);
    }

    @Test
    @DisplayName(value = "유저네임 중복으로 인한 회원가입 실패")
    void signup_fail_duplicateUsername() throws Exception {
        // given
        String username = "memberA";
        String password = "1234";
        SignupResponse response = authService.signup(username, password);

        // when && then
        assertThatThrownBy(() -> authService.signup(username, "12345"))
                .isInstanceOf(DuplicateUsernameException.class)
                .hasMessage("아이디 중복");
    }

    @Test
    @DisplayName(value = "로그인 성공")
    void login() throws Exception {
        // given
        String username = "memberA";
        String password = "1234";
        authService.signup(username, password);

        // when
        LoginResponse response = authService.login(username, password);

        // then
        assertThat(response.getUsername()).isEqualTo(username);
        assertThat(response.getMemberRating()).isEqualTo(1500);
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(3600);
    }

    @Test
    @DisplayName(value = "아이디 불일치")
    void login_fail_InvalidLogin_username() throws Exception {
        // given
        String username = "memberA";
        String password = "1234";
        authService.signup(username, password);
        
        // when && then
        assertThatThrownBy(() -> authService.login("memberAA", password))
                .isInstanceOf(InvalidLoginException.class)
                .hasMessage("아이디 또는 비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName(value = "비밀번호 불일치")
    void login_fail_InvalidLogin_password() throws Exception {
        // given
        String username = "memberA";
        String password = "1234";
        authService.signup(username, password);

        // when && then
        assertThatThrownBy(() -> authService.login(username, "1234444"))
                .isInstanceOf(InvalidLoginException.class)
                .hasMessage("아이디 또는 비밀번호가 일치하지 않습니다.");
    }



}