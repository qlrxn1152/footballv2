package daehoon.footballv2.teammatch.repository;

import daehoon.footballv2.teammatch.domain.TeamMatchResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamMatchResultRepository extends JpaRepository<TeamMatchResult, Long> {

    boolean existsByTeamMatchId(Long teamMatchId);

    Optional<TeamMatchResult> findByTeamMatchId(Long teamMatchId);
}
