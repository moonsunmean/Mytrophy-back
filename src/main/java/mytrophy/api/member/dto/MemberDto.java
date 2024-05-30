package mytrophy.api.member.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class MemberDto {

    private String role;
    private String name;
    private String password; // 비밀번호
    private String username;//loginId
    private String nickname; // 별명
    private String email; // 이메일
    private String steamId; // 스팀 ID 값
    private String loginType;
    private String profileImage; // 프로필 이미지
}
