package mytrophy.global.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import mytrophy.api.game.service.GameDataService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class GameDataScheduler {

    private final GameDataService gameDataService;
    private boolean downloadPossible = false;
    private boolean isContinue = false;
    private int downSize = 20;
    private int test = 1;
    private static boolean serverStart = false;


    public GameDataScheduler(GameDataService gameDataService) {
        this.gameDataService = gameDataService;
    }

    // 15일에 한 번 실행
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24 * 15) // 15일 간격으로 실행, ms 이기 때문에 * 1000을 해준다.
    public void autoGameDataDownFirstStep() throws JsonProcessingException {
        if(!serverStart){
            serverStart();
            return; // 서버가 처음 시작했을땐 동작 안함
        }

        // 스팀에서 전체 게임 목록을 DB에 저장
        gameDataService.receiveSteamGameList();
        // 맨 처음 실행 할 때는 이어서 진행하지 않는다
        this.isContinue = false;
        // DB의 게임목록을 이용해서 상세정보 받아오기 시작
        this.downloadPossible = true;
        autoGameDataDownSecondStep();
    }

    // 60초 간격으로 실행
    @Scheduled(fixedDelay = 1000 * 3)
    public void autoGameDataDownSecondStep() throws JsonProcessingException {
        if (!downloadPossible) return; // downloadPossible이 false이면 종료
        if(!serverStart) return; // 서버가 처음 시작했을땐 동작 안함


        System.out.println("~~~~~~~~~~~~~~~~~ 스케줄러 실행 !! ~~~~~~~~~~~~~~~~~");
        boolean downFinish = gameDataService.receiveSteamGameListByDb(downSize, isContinue);
        isContinue = true;

        downloadPossible = !downFinish;
        // 테스트용 로직
        test++;
        if (test >= 200) {
            downloadPossible = false;
        }
    }

    public void startManualDown(Boolean isContinue, int downSize) throws JsonProcessingException {
        this.downloadPossible = true;
        this.isContinue = isContinue;
        this.downSize = downSize;
        autoGameDataDownSecondStep();
    }
    public void serverStart() {
        this.serverStart = true;
    }
}
