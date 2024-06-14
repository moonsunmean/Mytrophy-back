package mytrophy.api.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.entity.MemberGame;
import mytrophy.api.member.repository.MemberGameRepository;
import mytrophy.api.member.repository.MemberRepository;
import mytrophy.global.handler.CustomException;
import mytrophy.global.handler.ErrorCodeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MemberGameService {
    @Value("${steam.api-key}")
    private String steamKey;

    private final MemberRepository memberRepository;
    private final MemberGameRepository memberGameRepository;

    public MemberGameService(MemberRepository memberRepository, MemberGameRepository memberGameRepository) {
        this.memberRepository = memberRepository;
        this.memberGameRepository = memberGameRepository;
    }

//    public JsonNode findMemberSteamGames(Long id) throws JsonProcessingException {
//        RestTemplate restTemplate = new RestTemplate();
//
//        // 디비에서 회원 정보 가져옴
//        Optional<Member> memberOptional = memberRepository.findById(id);
//        if (memberOptional.isEmpty()) {
//            throw new IllegalArgumentException("Invalid memberId: " + id);
//        }
//
//        Member member = memberOptional.get();
//        String steamId = member.getSteamId();
//
//        // Steam API 호출 URL 구성
//        String url = "https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/"
//                + "?key=" + steamKey
//                + "&steamid=" + steamId
//                + "&format=json";
//
//        // API 호출 및 응답 처리
//        String response = restTemplate.getForObject(url, String.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        return objectMapper.readTree(response);
//    }

    public JsonNode findMemberSteamGames(Long id) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        // 디비에서 회원 정보 가져옴
        Optional<Member> memberOptional = memberRepository.findById(id);
        if (memberOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid memberId: " + id);
        }

        Member member = memberOptional.get();
        String steamId = member.getSteamId();

        // Steam API 호출 URL 구성
        String url = "https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/"
            + "?key=" + steamKey
            + "&steamid=" + steamId
            + "&format=json";

        // API 호출 및 응답 처리
        String response = restTemplate.getForObject(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode games = objectMapper.readTree(response).path("response").path("games");

        // DB에 저장되지 않은 게임 확인
        List<JsonNode> notSavedGames = new ArrayList<>();
        for (JsonNode game : games) {
            Long gameId = game.path("appid").asLong();
            if (!memberGameRepository.existsById(gameId)) {
                // DB에 저장되지 않은 게임이면 예외를 발생시키지 않고 따로 리스트에 추가
                notSavedGames.add(game);
            }
        }

        // 만약 DB에 저장되지 않은 게임이 있는 경우 예외를 발생시킬 수도 있음
        if (!notSavedGames.isEmpty()) {
            throw new CustomException(ErrorCodeEnum.NOT_SAVED_GAME);
        }

        // DB에 저장되지 않은 게임이 없으면 조회된 게임들을 반환
        return games;
    }

    public void saveMemberSteamGameList(Long id) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        // 디비에서 회원 정보 가져옴
        Optional<Member> memberOptional = memberRepository.findById(id);
        if (memberOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid memberId: " + id);
        }

        Member member = memberOptional.get();
        String steamId = member.getSteamId();

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

    // 게임 업적 조회
    public JsonNode findMemberSteamGameAchievement(Long id, Long appId) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        // 디비에서 회원 정보 가져옴
        Optional<Member> memberOptional = memberRepository.findById(id);
        if (memberOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid memberId: " + id);
        }

        Member member = memberOptional.get();
        String steamId = member.getSteamId();

        // Steam API 호출 URL 구성
        String url = "https://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v0001/"
                + "?appid=" + appId
                + "&key=" + steamKey
                + "&steamid=" + steamId;

        // API 호출 및 응답 처리
        String response = restTemplate.getForObject(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(response);

        // 도전 과제가 없는 게임 처리
        JsonNode achievements = root.path("playerstats").path("achievements");
        if (achievements.isMissingNode() || !achievements.isArray()) {
            throw new IllegalArgumentException("This game does not provide achievements.");
        }

        return root;
    }
}
