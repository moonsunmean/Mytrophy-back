package mytrophy.api.game.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class GameApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private final GameDataService gameDataService;

    public GameApplicationListener(GameDataService gameDataService) {
        this.gameDataService = gameDataService;
    }

    // 서버가 처음 시작되면 한 번만 스팀에서 데이터를 받아온다.
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 실행하고자 하는 코드 작성
        try {
            gameDataService.receiveSteamGameList();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
//        gameDataService.readCategoryList();


    }

}
