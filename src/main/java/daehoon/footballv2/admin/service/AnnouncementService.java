package daehoon.footballv2.admin.service;

import daehoon.footballv2.admin.domain.Announcement;
import daehoon.footballv2.admin.dto.request.AnnouncementCreateRequest;
import daehoon.footballv2.admin.dto.response.AnnouncementDetailResponse;
import daehoon.footballv2.admin.dto.response.AnnouncementSummaryResponse;

import java.util.List;

// 게시판에 대한 ..
public interface AnnouncementService {

    AnnouncementDetailResponse createAnnouncement(Long memberId, AnnouncementCreateRequest request);

    List<AnnouncementSummaryResponse> findAnnouncements();

    AnnouncementDetailResponse findAnnouncementDetail(Long announcementId);



}
