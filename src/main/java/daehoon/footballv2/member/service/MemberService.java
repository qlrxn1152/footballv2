package daehoon.footballv2.member.service;

import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.member.dto.response.MemberDetailResponse;
import daehoon.footballv2.member.dto.response.MemberMeResponse;
import daehoon.footballv2.member.dto.response.MemberRankingResponse;

import java.util.List;

public interface MemberService {
    List<MemberRankingResponse> membersRanking();

    MemberDetailResponse findMemberDetail(Long memberId);

    MemberMeResponse findMyInfo(Long memberId);
}
