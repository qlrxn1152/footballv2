package daehoon.footballv2.teammatch.repository;

import daehoon.footballv2.teammatch.domain.TeamMatch;
import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface TeamMatchRepository extends JpaRepository<TeamMatch, Long> {

    boolean existsByHomeTeamIdAndStatus(Long homeTeamId, TeamMatchStatus status);

}
