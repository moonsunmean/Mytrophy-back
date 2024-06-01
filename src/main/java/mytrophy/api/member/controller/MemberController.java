package mytrophy.api.member.controller;

import mytrophy.api.image.service.ImageService;
import mytrophy.api.member.dto.MemberDto;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final ImageService imageService;

    public MemberController(MemberService memberService, ImageService imageService) {
        this.memberService = memberService;
        this.imageService = imageService;
    }

    // 중복 회원 검증
    @GetMapping("/checkUsername")
    public boolean isUsernameExists(@RequestParam String username) {
        return memberService.isUsernameExists(username);
    }

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<String> signupMember(@RequestBody MemberDto memberDto) {
        memberService.signupMember(memberDto);
        return new ResponseEntity<>("회원 가입 성공", HttpStatus.CREATED);
    }

    // 회원 조회
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable("id") Long id) {
        Member member = memberService.findMemberById(id);
        if (member == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(member);

    }

    // 회원 수정
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateMember(@PathVariable("id") Long id, @RequestBody MemberDto memberDto) {
        boolean isUpdated = memberService.updateMemberById(id, memberDto);
        if (isUpdated) {
            return new ResponseEntity<>("회원 수정 성공", HttpStatus.OK);
        }
        return new ResponseEntity<>("회원 수정 실패", HttpStatus.NOT_FOUND);
    }

    // 회원 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMember(@PathVariable("id") Long id) {
        boolean isDeleted = memberService.deleteMemberById(id);
        if (isDeleted) {
            return new ResponseEntity<>("회원 삭제 성공", HttpStatus.OK);
        }
        return new ResponseEntity<>("회원 삭제 실패", HttpStatus.NOT_FOUND);
    }

    // 회원 사진 추가
    @PostMapping("/files")
    public ResponseEntity<String> uploadOnlyFiles(@RequestPart(value = "file", required = false) List<MultipartFile> files) throws IOException {
        imageService.uploadFiles(files);
        return ResponseEntity.ok("파일 업로드 성공");
    }

    // 회원 사진 삭제
    @DeleteMapping("/files")
    public ResponseEntity<String> removeOnlyFiles(List<String> files) {
        imageService.removeFile(files);
        return ResponseEntity.ok("파일 삭제 성공");
    }
}
