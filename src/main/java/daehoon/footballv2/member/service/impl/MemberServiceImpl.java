package daehoon.footballv2.member.service.impl;

import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.member.repository.MemberRepository;
import daehoon.footballv2.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public Member findByMemberId(Long memberId) {
        return null;
    }

    @Override
    public Member findByUsername(String username) {
        return null;
    }

    @Override
    public List<Member> findAll() {
        return List.of();
    }
}
