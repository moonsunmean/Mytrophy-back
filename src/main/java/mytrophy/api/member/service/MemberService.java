package mytrophy.api.member.service;

import mytrophy.api.game.entity.Category;
import mytrophy.api.game.repository.CategoryRepository;
import mytrophy.api.member.dto.MemberDto;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CategoryRepository categoryRepository;

    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder, CategoryRepository categoryRepository) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.categoryRepository = categoryRepository;
    }

    // 회원 가입
    @Transactional
    public Member signupMember(MemberDto memberDto) {
        if (memberRepository.existsByUsername(memberDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + memberDto.getUsername());
        }

        Member signupMember = new Member();
        mapDtoToMember(memberDto, signupMember);
        signupMember.setPassword(encodePassword(memberDto.getPassword()));
        signupMember.setRole("ROLE_USER");

        return memberRepository.save(signupMember);
    }

    // 회원 조회
    public Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("다음 ID에 해당하는 회원을 찾을 수 없습니다: " + id));
    }

    // 회원 수정
    @Transactional
    public boolean updateMemberById(Long id, MemberDto memberDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("다음 ID에 해당하는 회원을 찾을 수 없습니다: " + id));

        mapDtoToMember(memberDto, member);
        member.setPassword(encodePassword(memberDto.getPassword()));

        memberRepository.save(member);
        return true;
    }

    // 회원 삭제
    @Transactional
    public boolean deleteMemberById(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new IllegalArgumentException("다음 ID에 해당하는 회원을 찾을 수 없습니다: " + id);
        }
        memberRepository.deleteById(id);
        return true;
    }

    private void mapDtoToMember(MemberDto memberDto, Member member) {
        member.setUsername(memberDto.getUsername());
        member.setName(memberDto.getName());
        member.setNickname(memberDto.getNickname());
        member.setEmail(memberDto.getEmail());
        member.setSteamId(memberDto.getSteamId());
        member.setLoginType(memberDto.getLoginType());
        member.setImagePath(memberDto.getImagePath());
        List<Category> categories = memberDto.getCategoryIds().stream()
                .map(categoryId -> categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId)))
                .collect(Collectors.toList());
        member.setCategories(categories);
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }
}
