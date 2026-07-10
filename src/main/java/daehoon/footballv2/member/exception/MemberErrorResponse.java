package daehoon.footballv2.member.exception;

import lombok.Getter;

@Getter
public class MemberErrorResponse {

    private final String code;
    private final String message;

    public MemberErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
