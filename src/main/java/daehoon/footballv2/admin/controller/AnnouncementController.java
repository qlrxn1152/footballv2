package daehoon.footballv2.admin.controller;

import daehoon.footballv2.admin.dto.request.AnnouncementCreateRequest;
import daehoon.footballv2.admin.dto.request.AnnouncementUpdateRequest;
import daehoon.footballv2.admin.dto.response.AnnouncementDetailResponse;
import daehoon.footballv2.admin.dto.response.AnnouncementSummaryResponse;
import daehoon.footballv2.admin.service.AnnouncementService;
import daehoon.footballv2.security.jwt.LoginMember;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping("/api/admin/announcements") // 게시판 작성 ...
    public ResponseEntity<AnnouncementDetailResponse> createAnnouncement(@Parameter(hidden = true) @LoginMember Long memberId, @Valid @RequestBody AnnouncementCreateRequest request) {
        AnnouncementDetailResponse response = announcementService.createAnnouncement(memberId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 공지사항 모두 조회 -> 모든 유저가 가능함
    @GetMapping("/api/announcements")
    public ResponseEntity<List<AnnouncementSummaryResponse>> findAnnouncements() {

        List<AnnouncementSummaryResponse> response = announcementService.findAnnouncements();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/announcements/{id}")
    public ResponseEntity<AnnouncementDetailResponse> findAnnouncementDetail(@PathVariable Long id) {

        AnnouncementDetailResponse response = announcementService.findAnnouncementDetail(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/api/admin/announcements/{announcementId}")
    public ResponseEntity<AnnouncementDetailResponse> updateAnnouncement(@Parameter(hidden = true) @LoginMember Long memberId, @PathVariable Long announcementId, @Valid @RequestBody AnnouncementUpdateRequest request) {
        AnnouncementDetailResponse response = announcementService.updateAnnouncement(memberId, announcementId, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/api/admin/announcements/{announcementId}")
    public ResponseEntity<Void> deleteAnnouncement(@Parameter(hidden = true) @LoginMember Long memberId, @PathVariable Long announcementId) {
        announcementService.deleteAnnouncement(memberId, announcementId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
