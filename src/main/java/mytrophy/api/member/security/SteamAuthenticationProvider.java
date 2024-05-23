package mytrophy.api.member.security;

import lombok.RequiredArgsConstructor;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.repository.MemberRepository;
import mytrophy.api.member.service.MemberService;
import mytrophy.api.member.service.SteamService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SteamAuthenticationProvider implements AuthenticationProvider {

    private final MemberService memberService;
    private final SteamService steamService;
    private final MemberRepository memberRepository;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String steamId = ((SteamAutenticationToken) authentication).getSteamId();

        Map<String, Object> userAttributes;
        try {
            userAttributes = steamService.getUserData(steamId);
            System.out.println(userAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Optional<Member> userOptional = memberService.findBySteamId(steamId);
        Member member = userOptional.orElseGet(() -> {
            String username = (String) userAttributes.get("personaname");
            String profile = (String) userAttributes.get("avatar");
            Member newMember = new Member();
            newMember.setName(username);
            newMember.setLoginType("steam");
            newMember.setSteamId(steamId);
            newMember.setLoginId(steamId);
            newMember.setProfileImage(profile);
            newMember.setRole("ROLE_USER");
            return memberRepository.save(newMember);
        });
        SteamUserPrincipal steamUserPrincipal = SteamUserPrincipal.create(member, userAttributes);

        return new SteamAutenticationToken(steamId, steamUserPrincipal, steamUserPrincipal.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(SteamAutenticationToken.class);
    }
}
