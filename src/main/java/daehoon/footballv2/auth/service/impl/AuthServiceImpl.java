package daehoon.footballv2.auth.service.impl;

import daehoon.footballv2.auth.dto.LoginResponse;
import daehoon.footballv2.auth.exception.exceptions.InvalidLoginException;
import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.auth.dto.SignupResponse;
import daehoon.footballv2.auth.exception.exceptions.DuplicateUsernameException;
import daehoon.footballv2.member.repository.MemberRepository;
import daehoon.footballv2.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;

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
                savedMember.getRating()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(String username, String password) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidLoginException("아이디 또는 비밀번호가 일치하지 않습니다."));

        log.info("password = {}", member.getPassword());

        if (!encoder.matches(password, member.getPassword())) {
            throw new InvalidLoginException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        return new LoginResponse(member.getId(), member.getUsername(), member.getRating());
    }
}
