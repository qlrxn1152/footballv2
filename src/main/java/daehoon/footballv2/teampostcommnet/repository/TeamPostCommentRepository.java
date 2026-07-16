package daehoon.footballv2.teampostcommnet.repository;

import daehoon.footballv2.teampostcommnet.domain.TeamPostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeamPostCommentRepository extends JpaRepository<TeamPostComment, Long> {

    @Query("select tpc from TeamPostComment tpc where tpc.teamPost.id = :postId and tpc.teamPost.team.id = :teamId")
    List<TeamPostComment> findAllTeamPostComment(Long postId, Long teamId);


    @Query("select tpc from TeamPostComment tpc where tpc.id = :commentId and tpc.teamPost.team.id = :teamId and tpc.teamPost.id = :postId")
    Optional<TeamPostComment> findTeamPostComment(Long commentId, Long teamId, Long postId);


}
