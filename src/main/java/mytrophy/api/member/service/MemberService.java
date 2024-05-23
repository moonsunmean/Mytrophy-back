package mytrophy.api.member.service;


import lombok.RequiredArgsConstructor;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    public Optional<Member> findBySteamId(String id) {
        return memberRepository.findBySteamId(id);
    }
}
