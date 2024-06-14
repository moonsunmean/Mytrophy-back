package mytrophy.global.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCodeEnum {

    NOT_EXISTS_MEMBER_ID(HttpStatus.NOT_FOUND, "M-001", "존재하지 않는 회원입니다."),
    UNAUTHORIZED(HttpStatus.FORBIDDEN, "M-002", "권한이 없습니다."),
    NOT_REVIEWED(HttpStatus.NOT_FOUND, "M-002", "해당 게임에 평가를 남기지 않았습니다."),

    NOT_EXISTS_ARTICLE_ID(HttpStatus.NOT_FOUND, "A-001", "존재하지 않는 게시글입니다."),

    NOT_EXISTS_COMMENT_ID(HttpStatus.NOT_FOUND, "C-001", "존재하지 않는 댓글입니다."),
    ALREADY_LIKED_COMMENT_ID(HttpStatus.BAD_REQUEST, "C-002", "이미 추천한 댓글입니다."),
    NOT_LIKED_COMMENT_ID(HttpStatus.BAD_REQUEST, "C-003", "추천하지 않은 댓글입니다."),
    NOT_EXISTS_PARENT_COMMENT_ID(HttpStatus.NOT_FOUND, "C-004", "존재하지 않는 부모 댓글입니다."),
    NOT_PARENT_COMMENT(HttpStatus.BAD_REQUEST, "C-005", "답글에는 추가 답글을 달 수 없습니다."),

    NOT_EXISTS_GAME_ID(HttpStatus.NOT_FOUND, "G-001", "해당하는 게임을 찾을 수 없습니다."),
    NOT_FOUND_GAME(HttpStatus.NOT_FOUND,"G-002","조회된 게임이 없습니다." ),

    NOT_DIFFERENT_PASSWORD(HttpStatus.BAD_REQUEST, "M-003", "비밀번호가 일치하지 않습니다."),
    NOT_SAVED_GAME(HttpStatus.BAD_REQUEST, "G-003", "Mytrophy에 저장되지 않은 게임입니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

    ErrorCodeEnum(HttpStatus httpStatus, String errorCode, String message) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }

    }
