package daehoon.footballv2.admin.repository;

import daehoon.footballv2.admin.domain.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
}
