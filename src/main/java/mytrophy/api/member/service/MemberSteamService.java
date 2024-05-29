package mytrophy.api.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.entity.MemberGame;
import mytrophy.api.member.repository.MemberGameRepository;
import mytrophy.api.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class MemberSteamService {
    @Value("${steam.api-key}")
    private String steamKey;

    private final MemberRepository memberRepository;
    private final MemberGameRepository memberGameRepository;

    public MemberSteamService(MemberRepository memberRepository, MemberGameRepository memberGameRepository) {
        this.memberRepository = memberRepository;
        this.memberGameRepository = memberGameRepository;
    }

    public void receiveMemberSteamGameList(Long id) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        // Retrieve Member from database
        Optional<Member> memberOptional = memberRepository.findById(id);
        if (memberOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid memberId: " + id);
        }

        Member member = memberOptional.get();
        Long steamId = member.getSteamId();
        System.out.println(steamId);

        // Steam API 호출 URL 구성
        String url = "https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/"
                + "?key=" + steamKey
                + "&steamid=" + steamId
                + "&format=json";

        // API 호출 및 응답 처리
        String response = restTemplate.getForObject(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(response);
        JsonNode games = root.path("response").path("games");

        if (games.isArray()) {
            for (JsonNode game : games) {
                Long appId = game.path("appid").asLong();
                int playtimeForever = game.path("playtime_forever").asInt();

                // 디비에 저장
                MemberGame memberGame = new MemberGame();
                memberGame.setMember(member);
                memberGame.setAppId(appId);
                memberGame.setPlaytimeForever(playtimeForever);

                memberGameRepository.save(memberGame);
            }
        }
    }
}