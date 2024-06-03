package mytrophy.global.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCodeEnum {

    NOT_EXISTS_ARTICLE_ID(HttpStatus.NOT_FOUND, "C-001", "존재하지 않는 게시글입니다."),
    NOT_EXISTS_MEMBER_ID(HttpStatus.NOT_FOUND, "C-002", "존재하지 않는 회원입니다."),
    NOT_EXISTS_COMMENT_ID(HttpStatus.NOT_FOUND, "C-003", "존재하지 않는 댓글입니다."),
    ALREADY_LIKED_COMMENT_ID(HttpStatus.BAD_REQUEST, "C-004", "이미 추천한 댓글입니다."),
    NOT_LIKED_COMMENT_ID(HttpStatus.BAD_REQUEST, "C-005", "추천하지 않은 댓글입니다."),
    UNAUTHORIZED(HttpStatus.FORBIDDEN, "C-006", "권한이 없습니다."),
    NOT_EXISTS_PARENT_COMMENT_ID(HttpStatus.NOT_FOUND, "C-007", "존재하지 않는 부모 댓글입니다."),
    NOT_PARENT_COMMENT(HttpStatus.BAD_REQUEST, "C-008", "답글에는 추가 답글을 달 수 없습니다."),
    NOT_EXISTS_GAME_ID(HttpStatus.NOT_FOUND, "C-009", "해당하는 게임을 찾을 수 없습니다."),;


    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

    ErrorCodeEnum(HttpStatus httpStatus, String errorCode, String message) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }

    }
