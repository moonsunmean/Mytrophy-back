package mytrophy.api.global.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mytrophy.api.member.dto.CustomOAuth2User;
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
            username = customUserDetails.getUsername();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            role = authorities.iterator().next().getAuthority();
        } else if (authentication.getPrincipal() instanceof SteamUserPrincipal) {
            SteamUserPrincipal steamUserDetails = (SteamUserPrincipal) authentication.getPrincipal();
            username = steamUserDetails.getUsername();
            role = "ROLE_STEAM_USER";
            System.out.println(username);
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        String token = jwtUtil.createJwt("refresh",username, role, 60 * 60 * 60L);
        System.out.println(token);
        response.addCookie(createCookie("refresh", token));

        if ("ROLE_STEAM_USER".equals(role)) {
            response.sendRedirect("/steam/profile");
        } else {
            response.sendRedirect("/mt");
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