package daehoon.footballv2.admin.exception;

import daehoon.footballv2.admin.exception.exceptions.NotAdminException;
import daehoon.footballv2.admin.exception.exceptions.NotFoundAnnouncementException;
import daehoon.footballv2.member.exception.exceptions.NotFoundMemberException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class AdminExceptionHandler {

    @ExceptionHandler(NotAdminException.class)
    public ResponseEntity<AdminErrorResponse> handleNotAdminException(NotAdminException ex) {
        log.warn("Admin Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AdminErrorResponse("NOT_ADMIN", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundAnnouncementException.class)
    public ResponseEntity<AdminErrorResponse> handleNotFoundAnnouncementException(NotFoundAnnouncementException ex) {
        log.warn("NotFound Announcement Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AdminErrorResponse("NOT_FOUND_ANNOUNCEMENT", ex.getMessage()));
    }





}
