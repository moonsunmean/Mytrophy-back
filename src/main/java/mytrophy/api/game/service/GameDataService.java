package mytrophy.api.game.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mytrophy.api.game.entity.*;
import mytrophy.api.game.enums.ReadType;
import mytrophy.api.game.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@Transactional
public class GameDataService {

    private final GameRepository gameRepository;
    private final AchievementRepository achievementRepository;
    private final CategoryRepository categoryRepository;
    private final ScreenshotRepository screenshotRepository;
    private final GameCategoryRepository gameCategoryRepository;
    private final GameDataRepository gameDataRepository;
    private final GameReadRepository gameReadRepository;

    // application.properties에서 설정된 값 주입
    @Value("${steam.api-key}")
    private String steamKey;

    @Autowired
    public GameDataService(GameRepository gameRepository, AchievementRepository achievementRepository,
                           CategoryRepository categoryRepository, ScreenshotRepository screenshotRepository,
                           GameCategoryRepository gameCategoryRepository, GameDataRepository gameDataRepository,
                           GameReadRepository gameReadRepository) {
        this.gameRepository = gameRepository;
        this.achievementRepository = achievementRepository;
        this.categoryRepository = categoryRepository;
        this.screenshotRepository = screenshotRepository;
        this.gameCategoryRepository = gameCategoryRepository;
        this.gameDataRepository = gameDataRepository;
        this.gameReadRepository = gameReadRepository;
    }

    // 스팀 게임 목록을 받아와 DB에 저장하는 메서드
    public void receiveSteamGameList() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://api.steampowered.com/ISteamApps/GetAppList/v2/";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        JsonNode rootNode = new ObjectMapper().readTree(response.getBody());
        JsonNode appsNode = rootNode.get("applist").get("apps");

        List<GameData> gameDataList = new ArrayList<>();
        List<Integer> gameDataListCheck = new ArrayList<>();

        for (JsonNode appNode : appsNode){
            int id = appNode.get("appid").asInt();
            if(!gameDataRepository.existsByAppId(id) && !gameDataListCheck.contains(id)){
                gameDataList.add(new GameData(null, id));
                gameDataListCheck.add(id);
            }
        }
        gameDataRepository.saveAll(gameDataList);
    }

    public Boolean receiveSteamGameListByDb(int size, boolean isContinue) throws JsonProcessingException {
        // 마지막에 저장한 appId 불러오기
        List<GameRead> gameReadList = gameReadRepository.findAll();

        GameRead gameRead =
                (!gameReadList.isEmpty())?gameReadList.get(0):new GameRead(1L,0);

        // 스팀에서 받아온 모든 게임목록 불러온 후 오름차순 정렬
        List<GameData> gameDataList = gameDataRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(GameData::getId))
                .toList();

        // 마지막에 저장한 appId가 null 이거나 사용자가 직접 선택한 경우 처음부터 순회하며 다운
        int startIndex = 0;
        if (gameRead != null && isContinue && gameRead.getLastAppId() != 0) {
            GameData gameData = gameDataRepository.findByAppId(gameRead.getLastAppId());
            if (gameData != null) {
                startIndex = gameDataList.indexOf(gameData) + 1;
            }
        }
        System.out.println("마지막 다운 위치 : " + (startIndex - 1));


        // DB에 있는 스팀게임 목록을 스팀에 요청하여 다운받기
        int count = 1;
        int currentCount = 0;
        for (int i = startIndex; i < gameDataList.size(); i++) {
            int appId = gameDataList.get(i).getAppId();
            System.out.println("다운받는 APP-ID : " + appId);
            System.out.println("다운받는 APP의 위치 : " + i);
            gameDetail(appId);
            gameReadRepository.save(new GameRead(1L,appId));
            if(count >= size) break;
            count++;
            currentCount = i;
        }

        if (currentCount == gameDataList.size()) {
            return true;
        }

        // false 로 바꾸기 테스트 하는동안 true
        return false;
    }

    // 스팀 게임 top100 목록을 저장하는 메서드
    public List<Integer> receiveTopSteamGameList(int size, String type) throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://steamspy.com/api.php?request=top100in2weeks";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        JsonNode rootNode = new ObjectMapper().readTree(response.getBody());

        int count = 1;
        List<Integer> appList = new ArrayList<>();

        for (JsonNode appNode : rootNode) {
            int appId = appNode.get("appid").asInt();
            System.out.println(appId);
            System.out.println(count);
            if(type.equals("read")){
                gameDetail(appId);
            }
            else {
                appList.add(appId);
            }

            if (count >= size) break;
            count++;
        }
        return appList;
    }

    // 특정 게임의 상세 정보를 받아와 DB에 저장하는 메서드
    public void gameDetail(int appId) throws JsonProcessingException {
        String url = "https://store.steampowered.com/api/appdetails?appids=" + appId + "&l=korean";
        JsonNode appNode = getAppNodeFromUrl(url, String.valueOf(appId));
        if (appNode == null) {
            return;
        }
        // 게임이 아닐경우 다음 앱 검색
        Boolean isSuccess = appNode.get("success").asBoolean();
        if(!isSuccess){
            return;
        }
        String type = appNode.get("data").get("type").asText();
        if(!type.equals("game")){
            return;
        }

        // JSON 데이터를 가지고 게임 엔티티를 생성하고 저장
        Game game = createGameFromJson(appNode.get("data"), appId);
        // 게임 가격이 null 이면 출시 예정 게임이므로 건너뛰기
        if (game.getPrice() == null) {
            return;
        }
        game.setAchievementList(achievementRepository.saveAll(saveGameAchievement(appId)));
        game.setScreenshotList(screenshotRepository.saveAll(saveGameScreenshot(appNode.get("data").get("screenshots"))));
        game = gameRepository.save(game);

        // 받아온 게임의 카테고리 연결하여 DB에 저장
        saveGameCategory(appNode.get("data").get("genres"),game);
    }

    // 주어진 URL로부터 JSON 데이터를 받아와서 해당 앱의 노드를 반환
    private JsonNode getAppNodeFromUrl(String url, String strId) {
        try {
            ResponseEntity<String> response = new RestTemplate().exchange(url, HttpMethod.GET, null, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode rootNode = new ObjectMapper().readTree(response.getBody());
                if(strId.equals("0")){
                    return rootNode.get("game");
                }
                return rootNode.get(strId);
            } else {
                // 서버에서 OK 상태 코드를 반환하지 않았을 때는 null을 반환
                return null;
            }
        } catch (Exception e) {
            // 예외가 발생했을 때는 null을 반환
            return null;
        }
    }
    // JSON 데이터에서 게임 정보를 추출하여 게임 엔티티를 생성
    private Game createGameFromJson(JsonNode appNode, int appId) {
        String name = appNode.hasNonNull("name") ? appNode.get("name").asText() : null;
        String description = appNode.hasNonNull("short_description") ? appNode.get("short_description").asText() : null;

        // 게임 개발사
        String gameDeveloper = "";
        JsonNode developersNode = appNode.get("developers");
        if (developersNode != null && developersNode.isArray() && developersNode.size() > 0) {
            for (JsonNode developerNode : developersNode) {
                gameDeveloper += developerNode.asText();
                gameDeveloper += "&";
            }
        }

        // 게임 공급사
        String gamePublisher = "";
        JsonNode publishersNode = appNode.get("publishers");
        if (publishersNode != null && publishersNode.isArray() && publishersNode.size() > 0) {
            for (JsonNode publisherNode : publishersNode) {
                gamePublisher += publisherNode.asText();
                gamePublisher += ",";
            }
        }

        // 지원하는 언어
        String languages = appNode.hasNonNull("supported_languages") ? appNode.get("supported_languages").asText() : null;
        List<Boolean> checkList = languagePosible(languages);
        Boolean enPosible = checkList.get(0);
        Boolean koPosible = checkList.get(1);
        Boolean jpPosible = checkList.get(2);

        // 출시 날짜
        String date = appNode.hasNonNull("release_date") ? appNode.get("release_date").get("date").asText() : null;

        // 추천수
        Integer recommandation = appNode.hasNonNull("recommendations") ? appNode.get("recommendations").get("total").asInt() : null;

        // 헤어 이미지
        String headerImagePath = appNode.hasNonNull("header_image") ? appNode.get("header_image").asText() : null;

        // 게임 가격
        Boolean isFree = appNode.hasNonNull("is_free") ? appNode.get("is_free").asBoolean() : null;
        Integer price;
        if (isFree != null && isFree) {
            price = 0;
        } else {
            price = appNode.hasNonNull("price_overview") ?
                    appNode.get("price_overview").get("final").asInt() / 100 : null;
        }

        // 컴퓨터 권장 사양
        JsonNode requirementHader = appNode.hasNonNull("pc_requirements") ? appNode.get("pc_requirements") : null;
        String requirement = requirementHader.hasNonNull("minimum") ? requirementHader.get("minimum").asText() : null;

        Long id = null;
        Game target = gameRepository.findByAppId(appId);

        if (target != null) id = target.getId();

        return new Game(id,appId,name,description,gameDeveloper,gamePublisher,requirement,price,date,recommandation,headerImagePath,koPosible,enPosible,jpPosible,null,null,null);
    }

    // 게임 업적을 받아와서 업적 리스트를 반환하는 메서드
    private List<Achievement> saveGameAchievement(int appId) {
        try {
            String url = "https://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v2/?key=" + steamKey + "&appid=" + appId + "&l=koreana";
            JsonNode rootNode = getAppNodeFromUrl(url, "0");
            JsonNode achievementsNode = (rootNode != null && rootNode.has("availableGameStats")) ?
                    rootNode.get("availableGameStats").get("achievements") :
                    null;
            return achievementsNode != null ? parseAchievements(achievementsNode) : Collections.emptyList();
        } catch (Exception e) {
            // 서버에서 OK 상태 코드를 반환하지 않았을 때는 null을 반환
            return null;
        }
    }


    // JSON 데이터에서 업적 정보를 추출하여 업적 리스트를 반환하는 메서드
    private List<Achievement> parseAchievements(JsonNode achievementsNode) {
        List<Achievement> achievementList = new ArrayList<>();
        boolean isExist;
        for (JsonNode achievementNode : achievementsNode) {
            String name = achievementNode.get("displayName").asText();
            String imagePath = achievementNode.get("icon").asText();
            isExist = achievementRepository.existsByName(name);
            if(!isExist)achievementList.add(new Achievement(null, name, imagePath));
        }
        return achievementList;
    }

    // JSON 데이터에서 스크린샷 정보를 추출하여 스크린샷 리스트를 반환하는 메서드
    private List<Screenshot> saveGameScreenshot(JsonNode appsNode) {
        List<Screenshot> screenshotList = new ArrayList<>();
        boolean isExist;
        if (appsNode == null) {
            return Collections.emptyList();
        }
        for (JsonNode appNode : appsNode) {
            String thumbnailImagePath = appNode.get("path_thumbnail").asText();
            String fullImagePath = appNode.get("path_full").asText();
            isExist = screenshotRepository.existsByThumbnailImagePathAndFullImagePath(thumbnailImagePath, fullImagePath);
            if(!isExist) screenshotList.add(new Screenshot(null, thumbnailImagePath, fullImagePath));
        }
        return screenshotList;
    }

    public List<Boolean> languagePosible(String text) {

        List<Boolean> list = new ArrayList<>();

        if (text == null) {
            list.add(false);
            list.add(false);
            list.add(false);
            return list;
        }

        // "영어"가 포함되어 있는지 검사
        if (text.contains("영어")) {
            list.add(true);
        } else {
            list.add(false);
        }

        // "한국어"가 포함되어 있는지 검사
        if (text.contains("한국어")) {
            list.add(true);
        } else {
            list.add(false);
        }

        // "일본어"가 포함되어 있는지 검사
        if (text.contains("일본어")) {
            list.add(true);
        } else {
            list.add(false);
        }

        return list;
    }

    public void readCategoryList() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Resource resource = new ClassPathResource("static/json/genres.json");
            InputStream inputStream = resource.getInputStream();
            JsonNode jsonNode = objectMapper.readTree(inputStream);

            Map<Integer, String> idNameMap = new HashMap<>();
            jsonNode.fields().forEachRemaining(entry -> {
                idNameMap.put(Integer.parseInt(entry.getKey()), entry.getValue().asText());
            });

            idNameMap.forEach((id, name) -> categoryRepository.save(saveCategoryToDb(id,name)));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Category saveCategoryToDb(int receiveId,String name) {
        Long id = Long.valueOf(receiveId);
        return new Category(id, name,null);
    }

    private void saveGameCategory(JsonNode appsNode, Game game) {
        // null 값이면 바로 종료
        if (appsNode == null) return;

        // 게임과 카테고리 연결
        for (JsonNode appNode : appsNode) {
            Long categoryId = appNode.get("id").asLong();
            if(!gameCategoryRepository.existsByGameIdAndCategoryId(game.getId(), categoryId)){
                Category existingCategory = categoryRepository.findById(categoryId).orElse(null);
                GameCategory gameCategory = new GameCategory();
                gameCategory.setGame(game);
                gameCategory.setCategory(existingCategory);
                gameCategoryRepository.save(gameCategory);
            }
        }
    }
}
