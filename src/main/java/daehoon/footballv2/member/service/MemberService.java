package daehoon.footballv2.member.service;

import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.member.dto.response.*;
import daehoon.footballv2.team.domain.TeamJoinRequestStatus;

import java.util.List;

public interface MemberService {
    List<MemberRankingResponse> membersRanking();

    MemberDetailResponse findMemberDetail(Long memberId);

    MemberMeResponse findMyInfo(Long memberId);

    List<MyTeamJoinRequestResponse> findMyTeamJoinRequests(Long memberId, TeamJoinRequestStatus status);

    MyTeamJoinRequestResponse cancelRequest(Long joinRequestId, Long memberId);

    TeamLeaveResponse leaveTeam(Long memberId);
}
