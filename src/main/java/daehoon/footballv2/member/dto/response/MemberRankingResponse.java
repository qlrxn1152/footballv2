package daehoon.footballv2.member.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class MemberRankingResponse {

    private int rank; // 몇등인지
    private Long memberId;
    private String username;
    private int rating;
    private Long teamId;
    private String teamName;

    // 팀이 없는경우
    public MemberRankingResponse(int rank, Long memberId, String username, int rating) {
        this.rank = rank;
        this.memberId = memberId;
        this.username = username;
        this.rating = rating;
        this.teamId = null;
        this.teamName = null;
    }

    // 팀이 있는경우
    public MemberRankingResponse(int rank, Long memberId, String username, int rating, Long teamId, String teamName) {
        this.rank = rank;
        this.memberId = memberId;
        this.username = username;
        this.rating = rating;

        this.teamId = teamId;
        this.teamName = teamName;
    }
}
