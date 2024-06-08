package mytrophy.api.member.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class MemberResponseDto {
    private Long id;
    private String username; // 여기서 username 이 로그인할 때 입력하는 로그인 아이디
    private String role; // 권한 (ROLE_USER, ROLE_ADMIN)
    private String name; // 이름
    private String nickname; // 별명
    private String email; // 이메일
    private String steamId; // 스팀 ID 값
    private String loginType; // 로그인 형태 (소셜로그인, 일반로그인)
    private String imagePath; // 프로필 이미지
    private List<Long> categoryIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
