package daehoon.footballv2.member.exception;

import daehoon.footballv2.auth.exception.AuthErrorResponse;
import daehoon.footballv2.member.exception.exceptions.NotFoundMemberException;
import daehoon.footballv2.team.exception.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class MemberExceptionHandler {

    @ExceptionHandler(NotFoundMemberException.class)
    public ResponseEntity<MemberErrorResponse> handleNotFoundMemberException(NotFoundMemberException ex) {
        log.warn("Not Found Member Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MemberErrorResponse("NOT_FOUND_MEMBER", ex.getMessage()));
    }



}
