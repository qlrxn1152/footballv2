package daehoon.footballv2.admin.service.impl;

import daehoon.footballv2.admin.domain.Announcement;
import daehoon.footballv2.admin.dto.request.AnnouncementCreateRequest;
import daehoon.footballv2.admin.dto.response.AnnouncementDetailResponse;
import daehoon.footballv2.admin.dto.response.AnnouncementSummaryResponse;
import daehoon.footballv2.admin.exception.exceptions.NotFoundAnnouncementException;
import daehoon.footballv2.admin.repository.AnnouncementRepository;
import daehoon.footballv2.admin.service.AnnouncementService;
import daehoon.footballv2.admin.validation.AnnouncementValidator;
import daehoon.footballv2.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementValidator announceMentValidator;

    @Override
    public AnnouncementDetailResponse createAnnouncement(Long memberId, AnnouncementCreateRequest request) {
        Member member = announceMentValidator.validateCheckAdmin(memberId); // 어드민 맞는지 확인

        Announcement announcement = new Announcement(member, request.getAnnouncementType(), request.getTitle(), request.getContent(), request.getVersion(), request.isPinned());

        announcementRepository.save(announcement);

        return new AnnouncementDetailResponse(announcement.getId(), announcement.getAnnouncementType(), announcement.getTitle(),  announcement.getContent(), announcement.getVersion(), announcement.isPinned());
    }

    @Override
    public List<AnnouncementSummaryResponse> findAnnouncements() {
        return announcementRepository.findAll()
                .stream()
                .map(announcement -> new AnnouncementSummaryResponse(
                        announcement.getId(),
                        announcement.getAnnouncementType(),
                        announcement.getTitle(),
                        announcement.getVersion(),
                        announcement.isPinned(),
                        announcement.getAuthorMember().getUsername(),
                        announcement.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public AnnouncementDetailResponse findAnnouncementDetail(Long announcementId) {
        Announcement announcement = announceMentValidator.validateAnnouncementExist(announcementId);

        return new AnnouncementDetailResponse(announcement.getId(), announcement.getAnnouncementType(), announcement.getTitle(), announcement.getContent(), announcement.getVersion(), announcement.isPinned());
    }


}
