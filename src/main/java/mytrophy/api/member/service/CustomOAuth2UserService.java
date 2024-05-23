package mytrophy.api.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mytrophy.api.member.Dto.*;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;



@RequiredArgsConstructor
@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else {

            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String type= oAuth2Response.getProvider();
        String snsId = oAuth2Response.getProviderId();

        Member existData = memberRepository.findByLoginId(snsId);

        // 새로운 회원인 경우
        if(existData==null){
            Member member = new Member();
            member.setLoginType(type);
            member.setLoginId(snsId);
            member.setEmail(oAuth2Response.getEmail());
            member.setName(oAuth2Response.getName());
            member.setProfileImage(oAuth2Response.getProfileImg());
            member.setRole("ROLE_USER");
            memberRepository.save(member);

            MemberDto memberDto = new MemberDto();
            memberDto.setLoginId(snsId);
            memberDto.setLoginType(type);
            memberDto.setName(oAuth2Response.getName());
            memberDto.setRole("ROLE_USER");

            return new CustomOAuth2User(memberDto);
        }
        //기존 회원인경우
        else{
            existData.setEmail(oAuth2Response.getEmail());
            existData.setName(oAuth2Response.getName());

            memberRepository.save(existData);

            MemberDto memberDto = new MemberDto();
            memberDto.setLoginId(existData.getLoginId());
            memberDto.setLoginType(existData.getLoginType());
            memberDto.setName(oAuth2Response.getName());
            memberDto.setRole(existData.getRole());



            return new CustomOAuth2User(memberDto);
        }

    }
}