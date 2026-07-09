package daehoon.footballv2.member.service;

import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.member.dto.response.MemberRankingResponse;

import java.util.List;

public interface MemberService {
    List<MemberRankingResponse> membersRanking();
}
