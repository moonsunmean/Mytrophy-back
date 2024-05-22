package mytrophy.domain.member.service;

import mytrophy.domain.member.dto.LoginDto;
import mytrophy.domain.member.dto.SignupDto;
import mytrophy.domain.member.entity.Member;
import mytrophy.domain.member.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public LoginService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public boolean LoginMember(LoginDto loginDto) {
        // 실제로는 사용자 인증 로직을 추가해야 합니다.
        // 여기서는 단순히 예시로 사용자 이름과 비밀번호가 'admin'일 때만 성공으로 간주합니다.
        return "admin".equals(loginDto.getUsername()) && "admin".equals(loginDto.getPassword());
    }
}
