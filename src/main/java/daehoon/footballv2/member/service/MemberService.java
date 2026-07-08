package daehoon.footballv2.member.service;

import daehoon.footballv2.member.domain.Member;

import java.util.List;

public interface MemberService {

    Member findByMemberId(Long memberId);

    Member findByUsername(String username);

    List<Member> findAll();
}
