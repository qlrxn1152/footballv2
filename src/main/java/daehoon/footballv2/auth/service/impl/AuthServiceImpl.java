package daehoon.footballv2.auth.service.impl;

import daehoon.footballv2.auth.dto.LoginResponse;
import daehoon.footballv2.auth.exception.MemberLoginException;
import daehoon.footballv2.auth.exception.NotFoundMemberException;
import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.auth.dto.SignupResponse;
import daehoon.footballv2.auth.exception.DuplicateUsernameException;
import daehoon.footballv2.member.repository.MemberRepository;
import daehoon.footballv2.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
                .orElseThrow(() -> new NotFoundMemberException("해당 아이디를 가진 회원이 없습니다."));

        boolean matches = encoder.matches(password, member.getPassword());

        if (!matches) {
            throw new MemberLoginException("비밀번호 불일치.");
        }

        return new LoginResponse(member.getId(), member.getUsername(), member.getRating());
    }
}
