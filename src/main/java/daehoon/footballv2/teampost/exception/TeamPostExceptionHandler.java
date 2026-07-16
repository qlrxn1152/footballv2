package daehoon.footballv2.teampost.exception;

import daehoon.footballv2.team.exception.TeamErrorResponse;
import daehoon.footballv2.team.exception.exceptions.NotPendingException;
import daehoon.footballv2.teammatch.exception.exceptions.*;
import daehoon.footballv2.teampost.exception.exceptions.NotFoundTeamPostException;
import daehoon.footballv2.teampost.exception.exceptions.NotSameAuthorMemberException;
import daehoon.footballv2.teampost.exception.exceptions.TeamPostUpdateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class TeamPostExceptionHandler {

    @ExceptionHandler(NotFoundTeamPostException.class)
    public ResponseEntity<TeamPostErrorResponse> handleNotFoundTeamPostException(NotFoundTeamPostException ex) {
        log.warn("NotFound TeamPost Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new TeamPostErrorResponse("NOT_FOUND_TEAM_POST", ex.getMessage()));
    }

    @ExceptionHandler(NotSameAuthorMemberException.class)
    public ResponseEntity<TeamPostErrorResponse> handleNotSameAuthorMemberException(NotSameAuthorMemberException ex) {
        log.warn("NotSame AuthorMember Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new TeamPostErrorResponse("NOT_SAME_AUTHOR_MEMBER", ex.getMessage()));
    }

    @ExceptionHandler(TeamPostUpdateException.class)
    public ResponseEntity<TeamPostErrorResponse> handleTeamPostUpdateException(TeamPostUpdateException ex) {
        log.warn("TeamPost Update Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TeamPostErrorResponse("UPDATE_TITLE", ex.getMessage()));
    }

}
