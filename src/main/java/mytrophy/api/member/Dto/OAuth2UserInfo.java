package mytrophy.api.member.Dto;

import jakarta.security.auth.message.AuthException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mytrophy.api.member.entity.Member;

import java.util.Map;

@Builder
@Getter
@Setter
public record OAuth2UserInfo(
        String name,
        String email,
        String profile_image
) {


    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) { // registration id별로 userInfo 생성
            case "google" -> ofGoogle(attributes);
            default ->  ofKakao(attributes);
        };
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profile_image((String) attributes.get("picture"))
                .build();
    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuth2UserInfo.builder()
                .name((String) profile.get("nickname"))
                .email((String) account.get("email"))
                .profile_image((String) profile.get("profile_image_url"))
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .email(email)
                .profile_image(profile_image)
                //.memberKey(KeyGenerator.generateKey())
                //.role(Role.USER)
                .build();
    }
}