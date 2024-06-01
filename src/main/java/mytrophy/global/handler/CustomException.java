package mytrophy.global.handler;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private final ErrorCodeEnum errorcode;

    public CustomException(ErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
        this.errorcode = errorCode;
    }
}
