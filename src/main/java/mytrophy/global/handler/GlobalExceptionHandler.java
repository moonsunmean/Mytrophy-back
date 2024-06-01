package mytrophy.global.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("CustomException", e);
        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorcode());

        return ResponseEntity.status(e.getErrorcode().getHttpStatus()).body(errorResponse);
    }
}
