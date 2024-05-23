package mytrophy.api.global.config;

import lombok.RequiredArgsConstructor;
import mytrophy.api.member.security.SteamAuthenticationProvider;
import mytrophy.api.member.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity

public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final SteamAuthenticationProvider steamAuthenticationProvider;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //disable 설정
        http
                .csrf((auth) -> auth.disable())//csrf disable
                .formLogin((auth) -> auth.disable())//From 로그인 방식 disable
                .httpBasic((auth) -> auth.disable());   //HTTP Basic 인증 방식 disable

        //oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .defaultSuccessUrl("/my", true)  // 로그인 성공 후 리디렉션 URL
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService)));

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/","/my", "/login/**","steam/login", "steam/login/redirect", "steam/failed").permitAll()
                        .anyRequest().authenticated()); // 로그인 사용자만 접근가능

        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(steamAuthenticationProvider);

        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}