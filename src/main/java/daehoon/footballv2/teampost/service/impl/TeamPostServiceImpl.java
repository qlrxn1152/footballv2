package daehoon.footballv2.teampost.service.impl;

import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.team.domain.Team;
import daehoon.footballv2.team.domain.TeamMember;
import daehoon.footballv2.team.validator.TeamValidator;
import daehoon.footballv2.teampost.domain.TeamPost;
import daehoon.footballv2.teampost.dto.request.TeamPostCreateRequest;
import daehoon.footballv2.teampost.dto.response.TeamPostDetailResponse;
import daehoon.footballv2.teampost.dto.response.TeamPostSummaryResponse;
import daehoon.footballv2.teampost.repository.TeamPostRepository;
import daehoon.footballv2.teampost.service.TeamPostService;
import daehoon.footballv2.teampost.validation.TeamPostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TeamPostServiceImpl implements TeamPostService {

    private final TeamPostRepository teamPostRepository;

    private final TeamPostValidator teamPostValidator;
    private final TeamValidator teamValidator;

    @Override
    public TeamPostDetailResponse createTeamPost(Long memberId, Long teamId, TeamPostCreateRequest request) {
        Team team = teamValidator.validateTeamExists(teamId);
        Member member = teamValidator.validateMemberExists(memberId);
        TeamMember teamMember = teamValidator.validateJoinedTeam(memberId);
        teamValidator.validateSameTeam(teamMember, teamId); // 해당팀에 속해있는지 확인

        TeamPost savedPost = teamPostRepository.save(new TeamPost(team, member, request.getTitle(), request.getContent()));

        return new TeamPostDetailResponse(savedPost.getId(), team.getId(), member.getId(), request.getTitle(), request.getContent(), member.getUsername(), savedPost.getCreatedAt(), savedPost.getUpdatedAt());
    }

    @Override
    public List<TeamPostSummaryResponse> findTeamPosts(Long memberId, Long teamId) {
        teamValidator.validateTeamExists(teamId);
        teamValidator.validateMemberExists(memberId);
        TeamMember teamMember = teamValidator.validateJoinedTeam(memberId);

        teamValidator.validateSameTeam(teamMember, teamId); // 해당팀에 속해있는지 확인

        return teamPostRepository.findAllByTeamId(teamId)
                .stream()
                .map(post -> new TeamPostSummaryResponse(
                        post.getId(),
                        post.getTeam().getId(),
                        post.getAuthorMember().getId(),
                        post.getTitle(),
                        post.getAuthorMember().getUsername(),
                        post.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public TeamPostDetailResponse findTeamPost(Long memberId, Long teamId, Long postId) {
        teamValidator.validateTeamExists(teamId);
        teamValidator.validateMemberExists(memberId);
        TeamMember teamMember = teamValidator.validateJoinedTeam(memberId);

        teamValidator.validateSameTeam(teamMember, teamId); // 해당팀에 속해있는지 확인

        TeamPost findPost = teamPostValidator.validateExistTeamPost(postId, teamId);

        return new TeamPostDetailResponse(
                findPost.getId(),
                findPost.getTeam().getId(),
                findPost.getAuthorMember().getId(),
                findPost.getTitle(),
                findPost.getContent(),
                findPost.getAuthorMember().getUsername(),
                findPost.getCreatedAt(),
                findPost.getUpdatedAt()
        );
    }


}
