package mytrophy.domain.member.controller;

import mytrophy.domain.member.dto.SignupDto;
import mytrophy.domain.member.service.SignupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
public class MemberController {
    private final SignupService signupService;

    public MemberController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signupProcess(SignupDto signupDto) {
        signupService.SignupProcess(signupDto);


        return new ResponseEntity<>("Signup successful", HttpStatus.CREATED);
    }
}
