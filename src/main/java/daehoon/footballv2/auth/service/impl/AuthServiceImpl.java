package daehoon.footballv2.auth.service.impl;

import daehoon.footballv2.auth.dto.response.login.LoginResponse;
import daehoon.footballv2.auth.exception.exceptions.InvalidLoginException;
import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.auth.dto.response.signup.SignupResponse;
import daehoon.footballv2.auth.exception.exceptions.DuplicateUsernameException;
import daehoon.footballv2.member.repository.MemberRepository;
import daehoon.footballv2.auth.service.AuthService;
import daehoon.footballv2.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public SignupResponse signup(String username, String password) {
        if (memberRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException("아이디 중복");
        }

        String encodedPassword = encoder.encode(password);
        Member savedMember = memberRepository.save(new Member(username, encodedPassword));

        return new SignupResponse(
                savedMember.getId(),
                savedMember.getUsername(),
                savedMember.getMemberRating()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(String username, String password) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidLoginException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!encoder.matches(password, member.getPassword())) {
            throw new InvalidLoginException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(member);

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtTokenProvider.getAccessTokenExpirationSeconds(),
                member.getId(),
                member.getUsername(),
                member.getMemberRating());
    }
}
