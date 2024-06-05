package mytrophy.api.member.dto;

import java.util.Map;

// window.location.href = "http://localhost:8080/oauth2/authorization/naver"
// -> 프론트단에서 로그인 버튼 클릭시 하이퍼 링크로 리다이렉트 해줘여함
public class NaverResponse implements OAuth2Response{

    private final Map<String, Object> attribute;

    public NaverResponse(Map<String, Object> attribute) {

        this.attribute = (Map<String, Object>) attribute.get("response");
    }

    @Override
    public String getProvider() {

        return "naver";
    }

    @Override
    public String getProviderId() {

        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {

        return attribute.get("email").toString();
    }

    @Override
    public String getName() {

        return attribute.get("name").toString();
    }
    public String getProfileImg() {

        return attribute.get("profile_image").toString();
    }
}