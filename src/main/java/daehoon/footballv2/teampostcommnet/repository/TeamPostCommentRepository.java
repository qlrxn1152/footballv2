package daehoon.footballv2.teampostcommnet.repository;

import daehoon.footballv2.teampostcommnet.domain.TeamPostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamPostCommentRepository extends JpaRepository<TeamPostComment, Long> {

    @Query("select tpc from TeamPostComment tpc where tpc.teamPost.id = :postId and tpc.teamPost.team.id = :teamId")
    List<TeamPostComment> findAllTeamPostComment(Long postId, Long teamId);
}
