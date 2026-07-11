package daehoon.footballv2.teammatch.repository;

import daehoon.footballv2.teammatch.domain.TeamMatch;
import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface TeamMatchRepository extends JpaRepository<TeamMatch, Long> {

    boolean existsByHomeTeamIdAndStatus(Long homeTeamId, TeamMatchStatus status);

    boolean existsByAwayTeamIdAndStatus(Long awayTeamId, TeamMatchStatus status);

    List<TeamMatch> findAllByStatusOrderByCreatedAtDesc(TeamMatchStatus status);

    List<TeamMatch> findAllByOrderByCreatedAtDesc();

}
