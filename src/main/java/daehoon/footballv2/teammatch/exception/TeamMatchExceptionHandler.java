package daehoon.footballv2.teammatch.exception;

import daehoon.footballv2.team.exception.TeamErrorResponse;
import daehoon.footballv2.team.exception.exceptions.NotPendingException;
import daehoon.footballv2.teammatch.exception.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class TeamMatchExceptionHandler {


    @ExceptionHandler(DuplicateTeamMatchException.class)
    public ResponseEntity<TeamErrorResponse> handleDuplicateTeamMatchException(DuplicateTeamMatchException ex) {
        log.warn("Duplicate TeamMatch Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new TeamErrorResponse("DUPLICATE_TEAM_MATCH", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundTeamMatchException.class)
    public ResponseEntity<TeamErrorResponse> handleNotFoundTeamMatchException(NotFoundTeamMatchException ex) {
        log.warn("NotFound TeamMatch Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TeamErrorResponse("NOT_FOUND_TEAM_MATCH", ex.getMessage()));
    }

    @ExceptionHandler(NotPendingException.class)
    public ResponseEntity<TeamErrorResponse> handleNotPendingException(NotPendingException ex) {
        log.warn("Not Pending Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TeamErrorResponse("NOT_PENDING", ex.getMessage()));
    }

    @ExceptionHandler(AlreadyExistTeamMatchException.class)
    public ResponseEntity<TeamErrorResponse> handleAlreadyExistTeamMatchException(AlreadyExistTeamMatchException ex) {
        log.warn("Already Exist TeamMatch Exception: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TeamErrorResponse("ALREADY_EXIST_TEAMMATCH", ex.getMessage()));
    }

    @ExceptionHandler(NotPendingTeamMatchException.class)
    public ResponseEntity<TeamErrorResponse> handleNotPendingTeamMatchException(NotPendingTeamMatchException ex) {
        log.warn("Not Pending TeamMatch Exception: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TeamErrorResponse("NOT_PENDING_MATCH", ex.getMessage()));
    }

    @ExceptionHandler(AlreadyExistMatchResultException.class)
    public ResponseEntity<TeamErrorResponse> handleAlreadyExistMatchResultException(AlreadyExistMatchResultException ex) {
        log.warn("Already Exist MatchResult Exception: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TeamErrorResponse("ALREADY_EXIST_MATCHRESULT", ex.getMessage()));
    }

    @ExceptionHandler(TeamMatchResultScoreException.class)
    public ResponseEntity<TeamErrorResponse> handleNotPendingTeamMatchException(TeamMatchResultScoreException ex) {
        log.warn("TeamMatchResult Score Exception: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TeamErrorResponse("MATCH_RESULT_SCORE", ex.getMessage()));
    }

    @ExceptionHandler(TeamMatchStatusException.class)
    public ResponseEntity<TeamErrorResponse> handleTeamMatchStatusException(TeamMatchStatusException ex) {
        log.warn("TeamMatch Status Exception: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TeamErrorResponse("MATCH_STATUS", ex.getMessage()));
    }



}
