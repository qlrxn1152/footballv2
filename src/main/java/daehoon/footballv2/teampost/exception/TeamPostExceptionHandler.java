package daehoon.footballv2.teampost.exception;

import daehoon.footballv2.team.exception.TeamErrorResponse;
import daehoon.footballv2.team.exception.exceptions.NotPendingException;
import daehoon.footballv2.teammatch.exception.exceptions.*;
import daehoon.footballv2.teampost.exception.exceptions.NotFoundTeamPostException;
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



}
