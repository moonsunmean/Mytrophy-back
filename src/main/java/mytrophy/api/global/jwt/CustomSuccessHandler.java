package mytrophy.api.global.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mytrophy.api.member.dto.CustomOAuth2User;
import mytrophy.api.member.security.SteamUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.io.IOException;

@Component

public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    public CustomSuccessHandler(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username;
        String role;

        if (authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
            username = customUserDetails.getName();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            role = authorities.iterator().next().getAuthority();
        } else if (authentication.getPrincipal() instanceof SteamUserPrincipal) {
            SteamUserPrincipal steamUserDetails = (SteamUserPrincipal) authentication.getPrincipal();
            username = steamUserDetails.getName();
            role = "ROLE_STEAM_USER";
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        String token = jwtUtil.createJwt(username, role, 60 * 60 * 60L);
        response.addCookie(createCookie("Authorization", token));

        if ("ROLE_STEAM_USER".equals(role)) {
            response.sendRedirect("/steam/profile");
        } else {
            response.sendRedirect("/my");
        }
//        //OAuth2User
//        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
//
//        String username = customUserDetails.getName();
//
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
//        GrantedAuthority auth = iterator.next();
//        String role = auth.getAuthority();
//
//        String token = jwtUtil.createJwt(username, role, 60*60*60L);
//
//        response.addCookie(createCookie("Authorization", token));
//        response.sendRedirect("http://localhost:8080/my"); // 로그인 성공시 다이렉트 페이지 http://localhost:3000/미정
//        //response.addHeader("Authorization", "Bearer " + token);
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