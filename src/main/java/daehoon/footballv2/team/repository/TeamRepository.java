package daehoon.footballv2.team.repository;

import daehoon.footballv2.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    boolean existsByTeamName(String teamName);

    List<Team> findAllByOrderByTeamRatingDesc();
}
