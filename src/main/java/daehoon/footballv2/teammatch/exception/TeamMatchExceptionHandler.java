package daehoon.footballv2.teammatch.exception;

import daehoon.footballv2.team.exception.TeamErrorResponse;
import daehoon.footballv2.teammatch.exception.exceptions.DuplicateTeamMatchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class TeamMatchExceptionHandler {

    @ExceptionHandler(DuplicateTeamMatchException.class)
    public ResponseEntity<TeamMatchErrorResponse> handleDuplicateTeamMatchException(DuplicateTeamMatchException ex) {
        log.warn("Duplicate TeamMatch Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TeamMatchErrorResponse("DUPLICATE_TEAM_MATCH", ex.getMessage()));
    }

}
