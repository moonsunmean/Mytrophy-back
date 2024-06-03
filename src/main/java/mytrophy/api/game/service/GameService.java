package mytrophy.api.game.service;

import com.google.api.gax.rpc.NotFoundException;
import mytrophy.api.game.dto.ResponseDTO;
import mytrophy.api.game.dto.ResponseDTO.GetGameAchievementDTO;
import mytrophy.api.game.dto.ResponseDTO.GetGameScreenshotDTO;
import mytrophy.api.game.dto.ResponseDTO.GetTopGameDTO;
import mytrophy.api.game.dto.ResponseDTO.GetAllGameDTO;
import mytrophy.api.game.dto.ResponseDTO.GetGameCategoryDTO;
import mytrophy.api.game.dto.ResponseDTO.GetGameDetailDTO;
import mytrophy.api.game.dto.ResponseDTO.GetSearchGameDTO;
import mytrophy.api.game.entity.Achievement;
import mytrophy.api.game.entity.Category;
import mytrophy.api.game.entity.Game;
import mytrophy.api.game.entity.Screenshot;
import mytrophy.api.game.repository.*;
import mytrophy.global.handler.CustomException;
import mytrophy.global.handler.ErrorCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GameService {
    private final GameRepository gameRepository;
    private final AchievementRepository achievementRepository;
    private final CategoryRepository categoryRepository;
    private final ScreenshotRepository screenshotRepository;
    private final GameCategoryRepository gameCategoryRepository;

    @Autowired
    public GameService(GameRepository gameRepository, AchievementRepository achievementRepository, CategoryRepository categoryRepository, ScreenshotRepository screenshotRepository, GameCategoryRepository gameCategoryRepository) {
        this.gameRepository = gameRepository;
        this.achievementRepository = achievementRepository;
        this.categoryRepository = categoryRepository;
        this.screenshotRepository = screenshotRepository;
        this.gameCategoryRepository = gameCategoryRepository;
    }

    public Page<GetAllGameDTO> getAllGameDTO(int page, int size) {
        return gameRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending())).map(
                game -> new GetAllGameDTO(game.getAppId(), game.getName(), game.getHeaderImagePath())
        );
    }

    public Page<GetTopGameDTO> getTopGameDTO(int page, int size,List<Integer> appList) {
        int rank = 1;
        List<GetTopGameDTO> topGameDTOList = new ArrayList<>();
        for (int appid : appList) {
            Game game = gameRepository.findByAppId(appid);
            GetTopGameDTO dto;
            if (game != null) {
                dto = new GetTopGameDTO(game.getAppId(), game.getName(), game.getHeaderImagePath(), rank);
            } else {
                dto = new GetTopGameDTO(null, null, null, rank);
            }
            topGameDTOList.add(dto);
            rank++;
        }

        int start = page * size;
        int end = Math.min(start + size, topGameDTOList.size());
        List<GetTopGameDTO> pageList = topGameDTOList.subList(start, end);

        return new PageImpl<>(pageList, PageRequest.of(page, size), pageList.size());
    }


    public GetGameDetailDTO getGameDetailDTO(Integer id) {

        if (!gameRepository.existsByAppId(id)) throw new CustomException(ErrorCodeEnum.NOT_EXISTS_GAME_ID);

        Game game = gameRepository.findByAppId(id);

        List<Category> categoryList = new ArrayList<>();
        game.getGameCategoryList().forEach(gameCategory -> categoryList.add(gameCategory.getCategory()));

        List<Screenshot> screenshotList = game.getScreenshotList();

        List<Achievement> achievementList = game.getAchievementList();

        List<GetGameCategoryDTO> getGameCategoryDTOList = categoryList.stream()
                .map(category -> new GetGameCategoryDTO(category.getId(), category.getName()))
                .collect(Collectors.toList());

        List<GetGameScreenshotDTO> getGameScreenshotDTOList = screenshotList.stream()
                .map(screenshot -> new GetGameScreenshotDTO(screenshot.getId(),screenshot.getThumbnailImagePath(),screenshot.getFullImagePath()))
                .collect(Collectors.toList());

        List<GetGameAchievementDTO> getGameAchievementDTOList = achievementList.stream()
                .map(achievement -> new GetGameAchievementDTO(achievement.getId(), achievement.getName(), achievement.getImagePath()))
                .collect(Collectors.toList());

        return new GetGameDetailDTO(
                game.getAppId(),
                game.getName(),
                game.getDescription(),
                game.getDeveloper(),
                game.getPublisher(),
                game.getRequirement(),
                game.getPrice(),
                game.getReleaseDate(),
                game.getRecommendation(),
                game.getHeaderImagePath(),
                game.getKoIsPosible(),
                game.getEnIsPosible(),
                game.getJpIsPosible(),
                getGameCategoryDTOList,
                getGameScreenshotDTOList,
                getGameAchievementDTOList
        );
    }

    public Page<GetSearchGameDTO> getSearchGameDTO(String keyword, int page, int size , Long categoryId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        if(categoryId == 0){
            return gameRepository.findGameByNameContaining(keyword == null ? "" : keyword, pageable).map(
                    game -> new GetSearchGameDTO(game.getAppId(), game.getName(), game.getHeaderImagePath()));

        }
        return gameRepository.findGameByNameContainingByCategoryId(keyword == null ? "" : keyword , pageable,categoryId).map(
                game -> new GetSearchGameDTO(game.getAppId(), game.getName(), game.getHeaderImagePath()));
    }
}
