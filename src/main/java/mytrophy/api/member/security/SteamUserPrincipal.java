package mytrophy.api.member.security;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mytrophy.api.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class SteamUserPrincipal implements UserDetails {
    private Long                                   id;
    private String                                 steamId;
    private String                                 name;
    private String                                 profileImg;
    private Map<String, Object>                    attributes;
    private Collection<? extends GrantedAuthority> authorities;



    public static SteamUserPrincipal create(Member member, Map<String, Object> attributes) {
        List<GrantedAuthority> authorities = Collections.
                singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        return new SteamUserPrincipal(member.getId(), member.getSteamId(), member.getName(), member.getImagePath(), Collections.unmodifiableMap(attributes), authorities);
    }



    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.steamId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}