package mytrophy.api.game.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import mytrophy.api.game.entity.Achievements;
import mytrophy.api.game.entity.Categories;
import mytrophy.api.game.entity.Games;
import mytrophy.api.game.entity.Languages;
import mytrophy.api.game.repository.AchievementsRepository;
import mytrophy.api.game.repository.CategoriesRepository;
import mytrophy.api.game.repository.GamesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class GameService {

    private final GamesRepository gamesRepository;
    private final AchievementsRepository achievementsRepository;
    private final CategoriesRepository categoriesRepository;

    @Value("${steam.api-key}")
    private String steamKey;

    @Value("${steam.access-key}")
    private String accessKey;

    @Autowired
    public GameService(GamesRepository gamesRepository,AchievementsRepository achievementsRepository,
                       CategoriesRepository categoriesRepository) {
        this.gamesRepository = gamesRepository;
        this.achievementsRepository = achievementsRepository;
        this.categoriesRepository = categoriesRepository;
    }

    // 스팀 게임 목록 db에 저장
    public void receiveSteamGameList() throws JsonProcessingException {

        // HTTP 요청을 보내고 응답을 받는 데 사용
        RestTemplate restTemplate = new RestTemplate();

        // 게임 목록 요청 API url
        String url = "http://api.steampowered.com/ISteamApps/GetAppList/v2/";

        // 게임 목록 요청하기
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

        // 응답에서 JSON 문자열 추출
        String responseBody = response.getBody();

        // Jackson ObjectMapper 생성
        ObjectMapper mapper = new ObjectMapper();

        // JSON 데이터 파싱
        JsonNode rootNode = mapper.readTree(responseBody);
        JsonNode appsNode = rootNode.get("applist").get("apps");

        // 각 앱의 정보 저장
        for (JsonNode appNode : appsNode) {
            Long appid = appNode.get("appid").asLong();
            String name = appNode.get("name").asText();

            Games game = new Games(appid, name,null);

            gamesRepository.save(game);
        }
    }

    // 스팀 게임 카테고리 DB에 저장
    public void receiveSteamCategoryList() throws JsonProcessingException {

        // HTTP 요청을 보내고 응답을 받는 데 사용
        RestTemplate restTemplate = new RestTemplate();

        // 카테고리 목록 요청 url
        String url = "https://api.steampowered.com/IStoreBrowseService/GetStoreCategories/v1/?access_token=" + accessKey + "&language=koreana";

        // 카테고리 목록 요청하기
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

        // 응답에서 JSON 문자열 추출
        String responseBody = response.getBody();

        // Jackson ObjectMapper 생성
        ObjectMapper mapper = new ObjectMapper();

        // JSON 데이터 파싱
        JsonNode rootNode = mapper.readTree(responseBody);
        JsonNode appsNode = rootNode.get("response").get("categories");

        // 각 앱의 정보 저장
        for (JsonNode appNode : appsNode) {
            Long id = appNode.get("categoryid").asLong();
            String koName = appNode.get("display_name").asText();
            String enName = appNode.get("internal_name").asText();

            Categories categorie = new Categories(id, koName, enName, null);

            categoriesRepository.save(categorie);
        }
    }

    public void gameDetail(int appId) throws JsonProcessingException {

        // HTTP 요청을 보내고 응답을 받는 데 사용
        RestTemplate restTemplate = new RestTemplate();

        // 게임 상세 정보 요청 url
        String url = "https://store.steampowered.com/api/appdetails?appids=" + appId + "&l=korean";

        // 게임 상세 정보 요청하기
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

        // 응답에서 JSON 문자열 추출
        String responseBody = response.getBody();

        // Jackson ObjectMapper 생성
        ObjectMapper mapper = new ObjectMapper();

        // JSON 데이터 파싱
        JsonNode rootNode = mapper.readTree(responseBody);
        JsonNode appNode = rootNode.get("data");

        // 각 앱의 정보 저장
        String detailDesc = appNode.get("detailed_description").asText();
        String shortDesc = appNode.get("short_description").asText();
        String aboutGame = appNode.get("about_the_game").asText();

        String languages = rootNode.get("supported_languages").asText();
        List<Languages> languagesList = GameLanguages(languages);

        String gameDeveloper = appNode.get("developers").asText();
        String gamePublisher = appNode.get("publishers").asText();

        String date = appNode.get("release_date").get("date").asText();
        // DateTimeFormatter를 사용하여 문자열을 LocalDateTime으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.KOREAN);
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);

        int recommandation = appNode.get("recommendations").get("total").asInt();
        String HeaderImagePath = appNode.get("header_image").asText();
        boolean isFree = appNode.get("is_free").asBoolean();
        int price = (isFree) ? 0 : appNode.get("price_overview").get("final").asInt() / 100;

    }

    public void GameAchievements(int appId) throws JsonProcessingException {

        // HTTP 요청을 보내고 응답을 받는 데 사용
        RestTemplate restTemplate = new RestTemplate();

        // 게임 업적 요청 API url
        String url = "https://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v2/?key="
        + steamKey + "&appid=" + appId;

        // 게임 업적 요청하기
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

        // 응답에서 JSON 문자열 추출
        String responseBody = response.getBody();

        // Jackson ObjectMapper 생성
        ObjectMapper mapper = new ObjectMapper();

        // JSON 데이터 파싱
        JsonNode rootNode = mapper.readTree(responseBody);
        JsonNode appsNode = rootNode.get("game").get("availableGameStats").get("achievements");

        // 각 앱의 정보 저장
        for (JsonNode appNode : appsNode) {
            String name = appNode.get("displayName").asText();
            String imagePath = appNode.get("icon").asText();

            Achievements achievement = new Achievements(null, name, imagePath);

            achievementsRepository.save(achievement);
        }
    }

    public List<Languages> GameLanguages(String languages) {

        // <strong>*</strong>를 제거하고 쉼표(,)를 기준으로 언어를 분리하여 리스트에 저장
        List<String> languagesList = new ArrayList<>(Arrays.asList(languages.split(", ")));
        languagesList.replaceAll(language -> language.replaceAll("<strong>\\*</strong>", ""));

        List<Languages> languageList = new ArrayList<>();

        // 결과를 엔티티로 변환
        for (String language : languagesList) {
            Languages lng = new Languages(null, language);
            languageList.add(lng);
        }

        return languageList;
    }

    public void GameScreenshots(int appId) {

    }

    public void GameCategories(int appId) {

    }

    public void GameGenres(int appId) {

    }



}
