package daehoon.footballv2.admin.exception;

import lombok.Getter;

@Getter
public class AdminErrorResponse {

    private final String code;
    private final String message;

    public AdminErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
