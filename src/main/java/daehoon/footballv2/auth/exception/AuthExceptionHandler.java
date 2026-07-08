package daehoon.footballv2.auth.exception;

import daehoon.footballv2.auth.exception.exceptions.DuplicateUsernameException;
import daehoon.footballv2.auth.exception.exceptions.InvalidLoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class AuthExceptionHandler {

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUsernameException(DuplicateUsernameException ex) {
        log.warn("Duplicate username : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("DUPLICATE_USERNAME", ex.getMessage()));
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<ErrorResponse> handleInvalidLoginException(InvalidLoginException ex) {
        log.warn("Invalid Login Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("INVALID_LOGIN", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("VALIDATION_ERROR", "아이디 또는 비밀번호 길이를 확인하세요."));
    }



}
