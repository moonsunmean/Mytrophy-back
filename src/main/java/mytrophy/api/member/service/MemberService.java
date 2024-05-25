package mytrophy.api.member.service;

import mytrophy.api.member.dto.MemberDto;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // 회원가입
    public void SignupMember(MemberDto memberDto) {

        Boolean isExist = memberRepository.existsByUsername(memberDto.getUsername());

        if (isExist) {
            return;
        }

        // 만약 회원가입 정보가 없으면 (처음 회원가입하면)
        Member signupMember = new Member();
        signupMember.setUsername(memberDto.getUsername());
        signupMember.setPassword(bCryptPasswordEncoder.encode(memberDto.getPassword()));
        signupMember.setRole("ROLE_USER");
        signupMember.setName(memberDto.getName());
        signupMember.setNickname(memberDto.getNickname());
        signupMember.setEmail(memberDto.getEmail());
        signupMember.setSteam_id(memberDto.getSteam_id());
        signupMember.setLogin_type(memberDto.getLogin_type());
        signupMember.setProfile_image(memberDto.getProfile_image());

        memberRepository.save(signupMember);
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
        updateMember.setName(memberDto.getName());
        updateMember.setNickname(memberDto.getNickname());
        updateMember.setEmail(memberDto.getEmail());
        updateMember.setSteam_id(memberDto.getSteam_id());
        updateMember.setLogin_type(memberDto.getLogin_type());
        updateMember.setProfile_image(memberDto.getProfile_image());

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
