package daehoon.footballv2.teammatch.exception;

import lombok.Getter;

@Getter
public class TeamMatchErrorResponse {

    private final String code;
    private final String message;

    public TeamMatchErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
