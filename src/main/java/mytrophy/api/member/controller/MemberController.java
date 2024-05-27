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
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final ImageService imageService;

    public MemberController(MemberService memberService, ImageService imageService) {
        this.memberService = memberService;
        this.imageService = imageService;
    }

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<Member> signupMember(@ModelAttribute MemberDto memberDto,
                                               @RequestPart(value = "file", required = false) List<MultipartFile> files) throws IOException {

        if (files != null) {
            List<String> url = imageService.uploadFiles(files);
            memberDto.setImagePath(url.toString());
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(memberService.signupMember(memberDto));
    }


    // 회원 조회
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable("id") Long id) {
        Optional<Member> member = Optional.ofNullable(memberService.findMemberById(id));
        return member.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
}
