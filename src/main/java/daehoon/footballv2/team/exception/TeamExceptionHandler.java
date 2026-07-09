package daehoon.footballv2.team.exception;

import daehoon.footballv2.auth.exception.AuthErrorResponse;
import daehoon.footballv2.auth.exception.exceptions.DuplicateUsernameException;
import daehoon.footballv2.auth.exception.exceptions.InvalidLoginException;
import daehoon.footballv2.team.exception.exceptions.AlreadyJoinedTeamException;
import daehoon.footballv2.team.exception.exceptions.DuplicateTeamJoinRequestException;
import daehoon.footballv2.team.exception.exceptions.DuplicateTeamNameException;
import daehoon.footballv2.team.exception.exceptions.NotFoundTeamException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class TeamExceptionHandler {

    @ExceptionHandler(AlreadyJoinedTeamException.class)
    public ResponseEntity<AuthErrorResponse> handleAlreadyJoinedTeamException(AlreadyJoinedTeamException ex) {
        log.warn("Already Joined Team Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new AuthErrorResponse("ALREADY_JOINED_TEAM", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateTeamNameException.class)
    public ResponseEntity<AuthErrorResponse> handleDuplicateTeamNameException(DuplicateTeamNameException ex) {
        log.warn("Duplicate TeamName Exception: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthErrorResponse("DUPLICATE_TEAM_NAME", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundTeamException.class)
    public ResponseEntity<AuthErrorResponse> handleNotFoundTeamException(NotFoundTeamException ex) {
        log.warn("Not Found Team Exception: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthErrorResponse("NOT_FOUND_TEAM", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateTeamJoinRequestException.class)
    public ResponseEntity<AuthErrorResponse> handleDuplicateTeamJoinRequestException(DuplicateTeamJoinRequestException ex) {
        log.warn("Duplicate Team Join Request Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthErrorResponse("DUPLICATE_TEAM_JOIN_REQUEST", ex.getMessage()));
    }



}
