package daehoon.footballv2.teampost.exception;

import lombok.Getter;

@Getter
public class TeamPostErrorResponse {

    private final String code;
    private final String message;

    public TeamPostErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
