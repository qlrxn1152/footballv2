package daehoon.footballv2.teampostcommnet.service.impl;

import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.team.domain.TeamMember;
import daehoon.footballv2.team.validator.TeamValidator;
import daehoon.footballv2.teampost.domain.TeamPost;
import daehoon.footballv2.teampost.validation.TeamPostValidator;
import daehoon.footballv2.teampostcommnet.domain.TeamPostComment;
import daehoon.footballv2.teampostcommnet.dto.request.TeamPostCommentCreateRequest;
import daehoon.footballv2.teampostcommnet.dto.response.TeamPostCommentResponse;
import daehoon.footballv2.teampostcommnet.repository.TeamPostCommentRepository;
import daehoon.footballv2.teampostcommnet.service.TeamPostCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamPostCommentServiceImpl implements TeamPostCommentService {

    private final TeamPostCommentRepository teamPostCommentRepository;


    private final TeamValidator teamValidator;
    private final TeamPostValidator teamPostValidator;

    @Override
    public TeamPostCommentResponse createTeamPostComment(Long teamId, Long postId, Long memberId, TeamPostCommentCreateRequest request) {
        // 팀있는지, 멤버있는지, 멤버가 팀에 속해있는지.
        teamValidator.validateTeamExists(teamId);
        Member member = teamValidator.validateMemberExists(memberId);
        TeamMember teamMember = teamValidator.validateJoinedTeam(memberId);
        teamValidator.validateSameTeam(teamMember, teamId);

        TeamPost post = teamPostValidator.validateExistTeamPost(postId, teamId);

        TeamPostComment savedComment = teamPostCommentRepository.save(new TeamPostComment(post, member, request.getContent()));

        return new TeamPostCommentResponse(
                savedComment.getId(),
                post.getId(),
                member.getId(),
                member.getUsername(),
                savedComment.getContent(),
                savedComment.getCreatedAt(),
                savedComment.getUpdatedAt()
        );

    }

    @Override
    public List<TeamPostCommentResponse> findTeamPostComments(Long teamId, Long postId, Long memberId) {
        teamValidator.validateTeamExists(teamId);
        Member member = teamValidator.validateMemberExists(memberId);
        TeamMember teamMember = teamValidator.validateJoinedTeam(memberId);
        teamValidator.validateSameTeam(teamMember, teamId);

        teamPostValidator.validateExistTeamPost(postId, teamId);


        return teamPostCommentRepository.findAllTeamPostComment(postId, teamId)
                .stream()
                .map(comment -> new TeamPostCommentResponse(
                        comment.getId(),
                        comment.getTeamPost().getId(),
                        comment.getAuthorMember().getId(),
                        comment.getAuthorMember().getUsername(),
                        comment.getContent(),
                        comment.getCreatedAt(),
                        comment.getUpdatedAt()
                ))
                .toList();



    }
}
