package mytrophy.global.handler;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private HttpStatus httpStatus;
    private String errorCode;
    private String errorMessage;

    @Builder
    public ErrorResponse(HttpStatus httpStatus, String errorCode, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static ErrorResponse of(ErrorCodeEnum code) {
        return ErrorResponse.builder()
                .httpStatus(code.getHttpStatus())
                .errorCode(code.getErrorCode())
                .errorMessage(code.getMessage())
                .build();
    }
}
