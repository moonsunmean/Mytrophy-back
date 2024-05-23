package mytrophy.api.game.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class GameApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private final GameService gameService;

    public GameApplicationListener(GameService gameService) {
        this.gameService = gameService;
    }

    // 서버가 처음 시작되면 한 번만 스팀에서 데이터를 받아온다.
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 실행하고자 하는 코드 작성
        try {
            // 스팀 게임 전체 게임 리스트 가져오기
//            gameService.receiveSteamGameList();
            // 스팀 게임 전체 카테고리 리스트 가져오기
//            gameService.receiveSteamCategoryList();
            // 스팀 상세 게임 업적목록 가져오기
//            gameService.GameAchievements(582010);
            gameService.gameDetail(582010);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
