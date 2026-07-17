package daehoon.footballv2.notification.domain;

import daehoon.footballv2.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberNotification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_member_id", nullable = false)
    private Member receiverMember;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    // 매치 아이디?
    private Long referenceId;

    private boolean read;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public MemberNotification(Member receiverMember, NotificationType type, String title, String content, Long referenceId) {
        this.receiverMember = receiverMember;
        this.type = type;
        this.title = title;
        this.content = content;
        this.referenceId = referenceId;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    public void read() {
        this.read = true;
    }


}
