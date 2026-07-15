package daehoon.footballv2.member.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    private static final int DEFAULT_MEMBER_RATING = 1500;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(name = "member_rating", nullable = false)
    private int memberRating;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "total_goal_count", nullable = false)
    @Min(value = 0)
    private Integer totalGoalCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_authority", nullable = false)
    private MemberAuthority authority;

    public Member(String username, String password) {
        this.username = username;
        this.password = password;
        this.memberRating = DEFAULT_MEMBER_RATING;
        this.totalGoalCount = 0;
        this.createdAt = LocalDateTime.now();
        this.authority = MemberAuthority.USER;
    }


    public void addGoals(Integer goalCount) {
        if (goalCount == null || goalCount < 1) {
            throw new IllegalArgumentException("추가할 골 수는 1 이상이어야 합니다.");
        }

        this.totalGoalCount += goalCount;
    }

    // 나중에 없애야함, 이 메서드는 테스트를 위한 메서드 // TODO :
    public void changeAuthority(MemberAuthority newAuthority) {
        this.authority = newAuthority;
    }

}
