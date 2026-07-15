package daehoon.footballv2.admin.validation;

import daehoon.footballv2.admin.domain.Announcement;
import daehoon.footballv2.admin.exception.exceptions.NotAdminException;
import daehoon.footballv2.admin.exception.exceptions.NotFoundAnnouncementException;
import daehoon.footballv2.admin.repository.AnnouncementRepository;
import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.member.domain.MemberAuthority;
import daehoon.footballv2.member.exception.exceptions.NotFoundMemberException;
import daehoon.footballv2.member.repository.MemberRepository;
import daehoon.footballv2.team.validator.TeamValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnnouncementValidator {

    private final AnnouncementRepository announcementRepository;
    private final MemberRepository memberRepository;

    public Member validateCheckAdmin(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException("멤버 조회 실패."));

        if (member.getAuthority() != MemberAuthority.ADMIN) {
            throw new NotAdminException("관리자 권한이 없습니다.");
        }

        return member;
    }

    public Announcement validateAnnouncementExist(Long announcementId) {
        return announcementRepository.findById(announcementId)
                .orElseThrow(() -> new NotFoundAnnouncementException("공지사항 게시물을 조회하지 못했습니다."));
    }


}
