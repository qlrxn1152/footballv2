package daehoon.footballv2.team.exception.exceptions;

public class DuplicateTeamJoinRequestException extends RuntimeException {
    public DuplicateTeamJoinRequestException(String message) {
        super(message);
    }
}
