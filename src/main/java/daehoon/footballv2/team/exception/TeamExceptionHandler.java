package daehoon.footballv2.team.exception;

import daehoon.footballv2.auth.exception.AuthErrorResponse;
import daehoon.footballv2.auth.exception.exceptions.DuplicateUsernameException;
import daehoon.footballv2.auth.exception.exceptions.InvalidLoginException;
import daehoon.footballv2.team.exception.exceptions.*;
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
    public ResponseEntity<TeamErrorResponse> handleAlreadyJoinedTeamException(AlreadyJoinedTeamException ex) {
        log.warn("Already Joined Team Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new TeamErrorResponse("ALREADY_JOINED_TEAM", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateTeamNameException.class)
    public ResponseEntity<TeamErrorResponse> handleDuplicateTeamNameException(DuplicateTeamNameException ex) {
        log.warn("Duplicate TeamName Exception: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TeamErrorResponse("DUPLICATE_TEAM_NAME", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundTeamException.class)
    public ResponseEntity<TeamErrorResponse> handleNotFoundTeamException(NotFoundTeamException ex) {
        log.warn("Not Found Team Exception: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TeamErrorResponse("NOT_FOUND_TEAM", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateTeamJoinRequestException.class)
    public ResponseEntity<TeamErrorResponse> handleDuplicateTeamJoinRequestException(DuplicateTeamJoinRequestException ex) {
        log.warn("Duplicate Team Join Request Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TeamErrorResponse("DUPLICATE_TEAM_JOIN_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(NotTeamLeaderException.class)
    public ResponseEntity<TeamErrorResponse> handleNotTeamLeaderException(NotTeamLeaderException ex) {
        log.warn("Not TeamLeader Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TeamErrorResponse("NOT_TEAM_LEADER", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundTeamJoinRequestException.class)
    public ResponseEntity<TeamErrorResponse> handleNotFoundTeamJoinRequestException(NotFoundTeamJoinRequestException ex) {
        log.warn("Not Found TeamJoinRequest Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TeamErrorResponse("NOT_FOUND_TEAM_JOIN_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(NotJoinedTeamException.class)
    public ResponseEntity<TeamErrorResponse> handleNotJoinedTeamException(NotJoinedTeamException ex) {
        log.warn("Not JoinedTeam Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TeamErrorResponse("NOT_JOINED_TEAM", ex.getMessage()));
    }

    @ExceptionHandler(NotSameTeamException.class)
    public ResponseEntity<TeamErrorResponse> handleNotSameTeamException(NotSameTeamException ex) {
        log.warn("Not SameTeam Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TeamErrorResponse("NOT_SAME_TEAM", ex.getMessage()));
    }

    @ExceptionHandler(TeamJoinRequestException.class)
    public ResponseEntity<TeamErrorResponse> handleTeamJoinRequestException(TeamJoinRequestException ex) {
        log.warn("TeamJoinRequest Exception: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TeamErrorResponse("TEAM_JOIN_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(NotPendingException.class)
    public ResponseEntity<TeamErrorResponse> handleNotPendingException(NotPendingException ex) {
        log.warn("Not Pending Exception: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TeamErrorResponse("NOT_PENDING_STATUS", ex.getMessage()));
    }

    @ExceptionHandler(CannotLeaveTeamLeaderException.class)
    public ResponseEntity<TeamErrorResponse> handleCannotLeaveTeamLeaderException(CannotLeaveTeamLeaderException ex) {
        log.warn("Cannot LeaveTeamLeader Exception: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new TeamErrorResponse("CAN_NOT_LEAVE_TEAMLEADER", ex.getMessage()));
    }

    @ExceptionHandler(SameTeamNameException.class)
    public ResponseEntity<TeamErrorResponse> handleSameTeamNameException(SameTeamNameException ex) {
        log.warn("SameTeamName Exception: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new TeamErrorResponse("SAME_TEAM_NAME", ex.getMessage()));
    }






}
