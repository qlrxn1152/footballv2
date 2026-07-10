package daehoon.footballv2.team.repository;

import daehoon.footballv2.team.domain.TeamMember;
import daehoon.footballv2.team.domain.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    boolean existsByMemberId(Long memberId);

    Optional<TeamMember> findByMemberId(Long memberId);

    List<TeamMember> findByTeamIdOrderByJoinedAtAsc(Long teamId);

    Optional<TeamMember> findLeaderMemberByTeamIdAndTeamRole(Long teamId, TeamRole teamRole);

    int countMemberByTeamId(Long teamId);

    void deleteAllByTeamId(Long teamId);
}
