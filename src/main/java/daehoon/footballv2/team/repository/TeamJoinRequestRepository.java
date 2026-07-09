package daehoon.footballv2.team.repository;

import daehoon.footballv2.team.domain.TeamJoinRequest;
import daehoon.footballv2.team.domain.TeamJoinRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, Long> {

    boolean existsByTeamIdAndMemberIdAndStatus(Long teamId, Long memberId, TeamJoinRequestStatus status);

    List<TeamJoinRequest> findByTeamIdAndStatusOrderByCreatedAtDesc(Long teamId, TeamJoinRequestStatus status);


}
