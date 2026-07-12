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
    public ResponseEntity<AuthErrorResponse> handleDuplicateUsernameException(DuplicateUsernameException ex) {
        log.warn("Duplicate username : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new AuthErrorResponse("DUPLICATE_USERNAME", ex.getMessage()));
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<AuthErrorResponse> handleInvalidLoginException(InvalidLoginException ex) {
        log.warn("Invalid Login Exception : {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthErrorResponse("INVALID_LOGIN", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AuthErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("요청 값을 확인하세요.");
        log.warn("Validation error: {}", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new AuthErrorResponse("VALIDATION_ERROR", message));
    }



}
