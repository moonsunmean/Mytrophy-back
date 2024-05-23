package mytrophy.api.member.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import mytrophy.api.member.service.MemberService;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;

//    @PostMapping("/signup")
//    public ResponseEntity<?> signUp(@RequestBody MemberSignupDto memberSignupDto) throws Exception {
//        Member member = memberService.signUp(memberSignupDto);
//        if (member != null) {
//            return ResponseEntity.ok(Collections.singletonMap("name", member.getName()));
//        } else {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 실패");
//        }
//    }


}
