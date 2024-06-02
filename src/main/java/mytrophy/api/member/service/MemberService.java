package mytrophy.api.member.service;


import lombok.RequiredArgsConstructor;
import mytrophy.api.game.entity.Category;
import mytrophy.api.game.repository.CategoryRepository;
import mytrophy.api.member.dto.MemberDto;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.transaction.annotation.Transactional;


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

    public Optional<Member> findBySteamId(String id) {
        return memberRepository.findBySteamId(id);
    }

    @Transactional
    public void signupMember(MemberDto memberDto) {

        if (memberRepository.existsByUsername(memberDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + memberDto.getUsername());
        }

        // 만약 회원가입 정보가 없으면 (처음 회원가입하면)
        Member signupMember = new Member();
        signupMember.setUsername(memberDto.getUsername());
        signupMember.setPassword(bCryptPasswordEncoder.encode(memberDto.getPassword()));
        signupMember.setRole("ROLE_USER");
        signupMember.setName(memberDto.getName());
        signupMember.setNickname(memberDto.getNickname());
        signupMember.setEmail(memberDto.getEmail());
        signupMember.setSteamId(memberDto.getSteamId());
        signupMember.setLoginType("mytrophy");
        signupMember.setImagePath(memberDto.getImagePath());

        memberRepository.save(signupMember);
    }






    // 회원 조회
    public Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("다음 ID에 해당하는 회원을 찾을 수 없습니다: " + id));
    }

    // 회원 수정
    public boolean updateMemberById(Long id, MemberDto memberDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("다음 ID에 해당하는 회원을 찾을 수 없습니다: " + id));


            member.setName(memberDto.getName());
            member.setEmail(memberDto.getEmail());
            member.setPassword(bCryptPasswordEncoder.encode(memberDto.getPassword()));
            member.setRole("ROLE_USER");
            member.setName(memberDto.getName());
            member.setNickname(memberDto.getNickname());
            member.setEmail(memberDto.getEmail());
            member.setSteamId(memberDto.getSteamId());
            member.setLoginType(memberDto.getLoginType());
            member.setImagePath(memberDto.getImagePath());
            memberRepository.save(member);
            return true;
    }



    @Transactional
    public boolean deleteMemberById(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new IllegalArgumentException("다음 ID에 해당하는 회원을 찾을 수 없습니다: " + id);
        }
        memberRepository.deleteById(id);
        return true;
    }
    public void linkSteamAccount(String username, String steamId) {
        System.out.println("linkSteam username : "+username);
        Member member = memberRepository.findByUsername(username);
        if (member == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        member.setSteamId(steamId);
        memberRepository.save(member);
    }




    private void mapDtoToMember(MemberDto memberDto, Member member) {
        member.setUsername(memberDto.getUsername());
        member.setName(memberDto.getName());
        member.setNickname(memberDto.getNickname());
        member.setEmail(memberDto.getEmail());
        member.setSteamId(memberDto.getSteamId());
        member.setLoginType(memberDto.getLoginType());
        member.setImagePath(memberDto.getImagePath());
        if (memberDto.getCategoryIds() != null) {
            List<Category> categories = memberDto.getCategoryIds().stream()
                .map(categoryId -> categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId)))
                .collect(Collectors.toList());
            member.setCategories(categories);
        }
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    // 회원 username으로 조회
    public Member findMemberByUsername(String username) {
        return memberRepository.findByUsername(username);
    }


}
