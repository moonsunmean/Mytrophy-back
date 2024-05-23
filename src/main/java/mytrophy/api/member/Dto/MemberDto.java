package mytrophy.api.member.Dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class MemberDto {

    private String role;
    private String name;
    private String loginId;
    private String loginType;
}
