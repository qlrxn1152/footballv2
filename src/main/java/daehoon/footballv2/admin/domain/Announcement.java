package daehoon.footballv2.admin.domain;

import daehoon.footballv2.member.domain.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Announcement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "announcement_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_member_id", nullable = false)
    private Member authorMember; // 작성자

    @Enumerated(EnumType.STRING)
    @Column(name = "announcement_type", nullable = false)
    private AnnouncementType announcementType;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String version;

    private boolean pinned; // 고정할건지: 고정 -> true

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 작성시간

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정시 나타낼 시간

    public Announcement(Member authorMember, AnnouncementType announcementType, String title, String content, String version, boolean pinned) {
        this.authorMember = authorMember;
        this.announcementType = announcementType;
        this.title = title;
        this.content = content;
        this.version = version;
        this.pinned = pinned;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateAnnouncement(AnnouncementType announcementType, String title, String content, String version, boolean pinned) {
        this.announcementType = announcementType;
        this.title = title;
        this.content = content;
        this.version = version;
        this.pinned = pinned;
        this.updatedAt = LocalDateTime.now();
    }
}
