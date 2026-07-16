package daehoon.footballv2.teampost.repository;

import daehoon.footballv2.teampost.domain.TeamPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamPostRepository extends JpaRepository<TeamPost, Long> {

    List<TeamPost> findAllByTeamId(Long teamId);

    Optional<TeamPost> findByIdAndTeamId(Long teamPostId, Long teamId);



    long countByTeamId(Long teamId);
}
