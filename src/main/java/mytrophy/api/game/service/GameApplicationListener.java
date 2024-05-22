package mytrophy.api.game.service;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class GameApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private final GameService gameService;

    public GameApplicationListener(GameService gameService) {
        this.gameService = gameService;
    }

    // 서버가 처음 시작되면 한 번만 스팀에서 데이터를 받아온다.
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 실행하고자 하는 코드 작성
        gameService.receiveSteamData();
    }

}
