package mytrophy.api.member.service;

import lombok.extern.slf4j.Slf4j;
import mytrophy.api.game.entity.Category;
import mytrophy.api.game.repository.CategoryRepository;
import mytrophy.api.member.dto.CategoryUpdateDto;
import mytrophy.api.member.dto.MemberDto;
import mytrophy.api.member.dto.MemberResponseDto;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.transaction.annotation.Transactional;


@Slf4j
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

    // 중복 아이디 검증
    @Transactional
    public boolean isUsernameExists(String username) {
        return memberRepository.existsByUsername(username);
    }

    // 회원 가입
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

    @Transactional
    public void updateMemberCategories(Long memberId, CategoryUpdateDto categoryUpdateDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("다음 ID에 해당하는 회원을 찾을 수 없습니다: " + memberId));

        List<Category> categories = categoryRepository.findAllById(categoryUpdateDto.getCategoryIds());
        member.setCategories(categories);
        memberRepository.save(member);
    }

    // 회원 조회
    @Transactional
    public MemberResponseDto getMemberDtoById(Long id) {
        Member member = memberRepository.findById(id).orElse(null);
        if (member == null) {
            return null;
        }
        return mapMemberToDto(member);
    }

    // 회원 수 조회
    @Transactional
    public long getMemberCount() {
        return memberRepository.count();
    }

    // 회원 리스트 조회
    @Transactional
    public Page<MemberResponseDto> findAll(Pageable pageable) {
        Page<Member> members = memberRepository.findAll(pageable);
        return members.map(this::mapMemberToDto);
    }

    // 회원 수정 (토큰)
    @Transactional
    public boolean updateMemberByUsername(String username, MemberDto memberDto) {
        Member member = memberRepository.findByUsername(username);
        if (member == null) {
            throw new IllegalArgumentException("다음 Username에 해당하는 회원을 찾을 수 없습니다: " + username);
        }

        mapDtoToMember(memberDto, member);
        member.setPassword(bCryptPasswordEncoder.encode(memberDto.getPassword()));
        memberRepository.save(member);
        return true;
    }

    // 회원 수정 (id)
    @Transactional
    public boolean updateMemberById(Long id, MemberDto memberDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("다음 ID에 해당하는 회원을 찾을 수 없습니다: " + id));

        mapDtoToMember(memberDto, member);
//        member.setPassword(bCryptPasswordEncoder.encode(memberDto.getPassword()));
        memberRepository.save(member);
        return true;
    }

    // 회원삭제 (토큰)
    @Transactional
    public boolean deleteMemberByUsername(String username) {
        if (!memberRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("다음 ID에 해당하는 회원을 찾을 수 없습니다: " + username);
        }
        memberRepository.deleteByUsername(username);
        return true;
    }

    // 회원 삭제 (id)
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

    private MemberResponseDto mapMemberToDto(Member member) {
        MemberResponseDto dto = new MemberResponseDto();
        dto.setId(member.getId());
        dto.setUsername(member.getUsername());
        dto.setName(member.getName());
        dto.setNickname(member.getNickname());
        dto.setEmail(member.getEmail());
        dto.setSteamId(member.getSteamId());
        dto.setLoginType(member.getLoginType());
        dto.setImagePath(member.getImagePath());
        dto.setRole(member.getRole());
        dto.setCreatedAt(member.getCreatedAt());
        dto.setUpdatedAt(member.getUpdatedAt());

        List<Long> categoryIds = member.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toList());
        dto.setCategoryIds(categoryIds);

        return dto;
    }



    private void mapDtoToMember(MemberDto memberDto, Member member) {
        if (memberDto.getUsername() != null) {
            member.setUsername(memberDto.getUsername());
        }
        if (memberDto.getPassword() != null) {
            member.setPassword(memberDto.getPassword());
        }
        if (memberDto.getRole() != null) {
            member.setRole(memberDto.getRole());
        }
        if (memberDto.getName() != null) {
            member.setName(memberDto.getName());
        }
        if (memberDto.getNickname() != null) {
            member.setNickname(memberDto.getNickname());
        }
        if (memberDto.getEmail() != null) {
            member.setEmail(memberDto.getEmail());
        }
        if (memberDto.getSteamId() != null) {
            member.setSteamId(memberDto.getSteamId());
        }
        if (memberDto.getLoginType() != null) {
            member.setLoginType(memberDto.getLoginType());
        }
        if (memberDto.getImagePath() != null) {
            member.setImagePath(memberDto.getImagePath());
        }
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    // 회원 username으로 조회
    @Transactional(readOnly = true)
    public MemberResponseDto findMemberDtoByUsername(String username) {
        Member member = memberRepository.findByUsernameWithCategories(username)
                .orElseThrow(() -> new IllegalArgumentException("다음 Username에 해당하는 회원을 찾을 수 없습니다: " + username));
        return mapMemberToDto(member);
    }
    // 회원 username으로 조회
    @Transactional(readOnly = true)
    public Member findMemberByUsername(String username) {
        return memberRepository.findByUsername(username);
    }


}
