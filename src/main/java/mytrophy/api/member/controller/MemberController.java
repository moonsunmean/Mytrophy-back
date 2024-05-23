package mytrophy.api.member.controller;

import mytrophy.api.member.dto.SignupDto;
import mytrophy.api.member.service.LoginService;
import mytrophy.api.member.service.SignupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final SignupService signupService;
    private final LoginService loginService;

    public MemberController(SignupService signupService, LoginService loginService) {
        this.signupService = signupService;
        this.loginService = loginService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signupMember(SignupDto signupDto) {
        signupService.SignupMember(signupDto);

        return new ResponseEntity<>("회원가입 성공", HttpStatus.CREATED);
    }

//    @PostMapping("/login")
//    public ResponseEntity<String> loginMember(LoginDto loginDto) {
//        boolean isAuthenticated = loginService.LoginMember(loginDto);
//
//        if (isAuthenticated) {
//            return new ResponseEntity<>("로그인 성공", HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("아이디 혹은 비밀번호가 잘못되었습니다.", HttpStatus.UNAUTHORIZED);
//        }
//    }
}
