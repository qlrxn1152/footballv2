package daehoon.footballv2.teammatch.repository;

import daehoon.footballv2.teammatch.domain.TeamMatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMatchRepository extends JpaRepository<TeamMatch, Long> {
}
