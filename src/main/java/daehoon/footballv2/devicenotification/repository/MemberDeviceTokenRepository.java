package daehoon.footballv2.devicenotification.repository;

import daehoon.footballv2.devicenotification.domain.MemberDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberDeviceTokenRepository extends JpaRepository<MemberDeviceToken, Long> {

    Optional<MemberDeviceToken> findByToken(String token);

    Optional<MemberDeviceToken> findByMemberIdAndToken(Long memberId, String token);

    void deleteByMemberIdAndToken(Long memberId, String token);

    List<MemberDeviceToken> findAllByMemberId(Long memberId);
}
