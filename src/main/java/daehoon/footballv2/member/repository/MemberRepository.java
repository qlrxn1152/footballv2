package daehoon.footballv2.member.repository;

import daehoon.footballv2.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByUsername(String username);

    Optional<Member> findByUsername(String username);

    List<Member> findAllByOrderByMemberRatingDesc();


}
