package mytrophy.api.member.service;

import mytrophy.api.member.dto.MemberDto;
import mytrophy.api.member.dto.SignupDto;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // 회원가입
    public void SignupMember(SignupDto signupDto) {
        String username = signupDto.getUsername();
        String password = signupDto.getPassword();

        Boolean isExist = memberRepository.existsByUsername(username);

        if (isExist) {
            return;
        }

        // 만약 회원가입 정보가 없으면 (처음 회원가입하면)
        Member createMember = new Member();
        createMember.setUsername(username);
        createMember.setPassword(bCryptPasswordEncoder.encode(password));
        createMember.setRole("ROLE_USER");

        memberRepository.save(createMember);
    }

    // 회원 조회
    public Member GetMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("다음 ID에 해당하는 회원을 찾을 수 없습니다: " + id));
    }

    // 회원 수정
    public void UpdateMemberById(Long id, MemberDto memberDto) {
        Member updateMember = new Member();
        updateMember.setId(id);
        updateMember.setPassword(bCryptPasswordEncoder.encode(memberDto.getPassword()));
        updateMember.setRole("ROLE_USER");

        memberRepository.save(updateMember);
    }

    // 회원 삭제
    public void DeleteMemberById(Long id) {
        if(memberRepository.existsById(id)) {
            memberRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("다음 ID에 해당하는 회원을 찾을 수 없습니다: " + id);
        }
    }
}
