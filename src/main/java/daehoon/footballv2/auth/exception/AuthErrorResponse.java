package daehoon.footballv2.auth.exception;

import lombok.Getter;

@Getter
public class AuthErrorResponse {

    private final String code;
    private final String message;

    public AuthErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
