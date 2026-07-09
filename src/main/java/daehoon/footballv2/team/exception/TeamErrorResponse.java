package daehoon.footballv2.team.exception;

import lombok.Getter;

@Getter
public class TeamErrorResponse {

    private final String code;
    private final String message;

    public TeamErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
