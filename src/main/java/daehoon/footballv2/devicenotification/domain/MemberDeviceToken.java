package daehoon.footballv2.devicenotification.domain;

import daehoon.footballv2.member.domain.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDeviceToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_device_token_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "device_token", nullable = false, length = 512)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_platform", nullable = false, length = 20)
    private DevicePlatform platform;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public MemberDeviceToken(Member member, String token, DevicePlatform platform) {
        this.member = member;
        this.token = token;
        this.platform = platform;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void refreshRegistration(Member member, DevicePlatform platform) {
        this.member = member;
        this.platform = platform;
        this.updatedAt = LocalDateTime.now();
    }
}
