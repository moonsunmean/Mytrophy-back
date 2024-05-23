package mytrophy.api.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberSignupDto {

    private String name;
    private String ninkname;
    private String email;
    private String login_id;
    private String password;
}
