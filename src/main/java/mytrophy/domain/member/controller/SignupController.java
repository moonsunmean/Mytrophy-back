package mytrophy.domain.member.controller;

import mytrophy.domain.member.dto.SignupDto;
import mytrophy.domain.member.service.SignupService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignupController {
    private final SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping("/signup")
    public String signupProcess(SignupDto signupDto) {
        signupService.SignupProcess(signupDto);

        return "ok";
    }
}
