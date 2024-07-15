package mytrophy.global.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import mytrophy.api.member.dto.CustomOAuth2User;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.repository.MemberRepository;
import mytrophy.api.member.security.SteamUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.io.IOException;

@Component
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;
    public CustomSuccessHandler(JWTUtil jwtUtil,MemberRepository memberRepository) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username;
        String role;

        if (authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
            username = customUserDetails.getUsername();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            role = authorities.iterator().next().getAuthority();
        } else if (authentication.getPrincipal() instanceof SteamUserPrincipal) {
            SteamUserPrincipal steamUserDetails = (SteamUserPrincipal) authentication.getPrincipal();
            username = steamUserDetails.getUsername();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            role = authorities.iterator().next().getAuthority();
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }
        Member AccessUser = memberRepository.findByUsername(username);
        boolean firstLogin = AccessUser.isFirstLogin();
        String token = jwtUtil.createJwt("refresh",username, role, 60 * 60 * 60L);
        response.addCookie(createCookie("refresh", token));

        String redirectUrl = String.format("http://34.64.52.132:3000/token-reissue/?firstLogin=%b", firstLogin);
        response.sendRedirect(redirectUrl);
        if (firstLogin) {
            AccessUser.setFirstLogin(false);
            memberRepository.save(AccessUser);
        }


        response.setStatus(HttpStatus.OK.value());



    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}