package mytrophy.api.game.service;


import mytrophy.api.article.entity.Article;
import mytrophy.api.article.enumentity.Header;
import mytrophy.api.article.repository.ArticleRepository;
import mytrophy.api.article.service.ArticleService;
import mytrophy.api.game.dto.RequestDTO;
import mytrophy.api.game.dto.RequestDTO.SearchGameRequestDTO;
import mytrophy.api.game.dto.ResponseDTO.GetGameAchievementDTO;
import mytrophy.api.game.dto.ResponseDTO.GetGameScreenshotDTO;
import mytrophy.api.game.dto.ResponseDTO.GetGameCategoryDTO;
import mytrophy.api.game.dto.ResponseDTO.GetTopGameDTO;
import mytrophy.api.game.dto.ResponseDTO.GetGameDetailDTO;
import mytrophy.api.game.dto.RequestDTO.UpdateGameRequestDTO;
import mytrophy.api.game.dto.RequestDTO.UpdateGameCategoryDTO;
import mytrophy.api.game.entity.*;
import mytrophy.api.game.enums.Positive;
import mytrophy.api.game.querydsl.GameQueryRepository;
import mytrophy.api.game.repository.*;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.repository.MemberRepository;
import mytrophy.api.member.service.MemberService;
import mytrophy.global.handler.CustomException;
import mytrophy.global.handler.ErrorCodeEnum;
import mytrophy.global.jwt.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class GameService {
    private final GameRepository gameRepository;
    private final GameQueryRepository gameQueryRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ArticleService articleService;
    private final ArticleRepository articleRepository;
    private final GameCategoryRepository gameCategoryRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public GameService(GameRepository gameRepository, GameQueryRepository gameQueryRepository, MemberService memberService, MemberRepository memberRepository, ArticleService articleService, ArticleRepository articleRepository, GameCategoryRepository gameCategoryRepository, CategoryRepository categoryRepository) {
        this.gameRepository = gameRepository;
        this.gameQueryRepository = gameQueryRepository;
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.articleService = articleService;
        this.articleRepository = articleRepository;
        this.gameCategoryRepository = gameCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<GetGameDetailDTO> getAllGameDTO(int page, int size) {
        Page<Game> gamePage = gameRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
        return gamePage.map(this::mapGameToDTO);
    }

    public Page<GetTopGameDTO> getTopGameDTO(int page, int size,List<Integer> appList) {
        int rank = 1;
        List<GetTopGameDTO> topGameDTOList = new ArrayList<>();
        for (int appid : appList) {
            Game game = gameRepository.findByAppId(appid);
            GetTopGameDTO dto;
            if (game != null) {
                List<Category> categoryList = new ArrayList<>();
                game.getGameCategoryList().forEach(gameCategory -> categoryList.add(gameCategory.getCategory()));
                List<GetGameCategoryDTO> getGameCategoryDTOList = categoryList.stream()
                        .map(category -> new GetGameCategoryDTO(category.getId(), category.getName()))
                        .collect(Collectors.toList());

                List<Screenshot> screenshotList = game.getScreenshotList();
                List<Achievement> achievementList = game.getAchievementList();
                List<GetGameScreenshotDTO> getGameScreenshotDTOList = screenshotList.stream()
                        .map(screenshot -> new GetGameScreenshotDTO(screenshot.getId(),screenshot.getThumbnailImagePath(),screenshot.getFullImagePath()))
                        .collect(Collectors.toList());

                List<GetGameAchievementDTO> getGameAchievementDTOList = achievementList.stream()
                        .map(achievement -> new GetGameAchievementDTO(achievement.getId(), achievement.getName(), achievement.getImagePath(),achievement.getHidden(),achievement.getDescription()))
                        .collect(Collectors.toList());

                dto = new GetTopGameDTO(game.getAppId(), game.getName(), game.getDescription(),game.getDeveloper(),game.getPublisher(),game.getRequirement(), game.getPrice(), game.getReleaseDate(),game.getRecommendation(),game.getPositive(),game.getHeaderImagePath(),game.getKoIsPosible(),game.getEnIsPosible(),game.getJpIsPosible(),getGameCategoryDTOList,getGameScreenshotDTOList,getGameAchievementDTOList,rank);

            } else {
                dto = new GetTopGameDTO();
                dto.setRank(rank);
            }
            topGameDTOList.add(dto);
            rank++;
        }

        int start = page * size;
        int end = Math.min(start + size, topGameDTOList.size());
        List<GetTopGameDTO> pageList = topGameDTOList.subList(start, end);

        return new PageImpl<>(pageList, PageRequest.of(page, size), pageList.size());
    }




    public GetGameDetailDTO getGameDetailDTO(Integer appId) {

        if (!gameRepository.existsByAppId(appId)) throw new CustomException(ErrorCodeEnum.NOT_EXISTS_GAME_ID);

        Game game = gameRepository.findByAppId(appId);

        return mapGameToDTO(game);
    }

    public boolean updateGameDetail(Integer appId, UpdateGameRequestDTO updateGameRequestDTO) {
        if (!gameRepository.existsByAppId(appId)) throw new CustomException(ErrorCodeEnum.NOT_EXISTS_GAME_ID);

        Game targetGame = gameRepository.findByAppId(appId);
        if (updateGameRequestDTO.getName() != null) {
            targetGame.setName(updateGameRequestDTO.getName());
        }
        if (updateGameRequestDTO.getDescription() != null) {
            targetGame.setDescription(updateGameRequestDTO.getDescription());
        }
        if (updateGameRequestDTO.getPrice() != null) {
            targetGame.setPrice(updateGameRequestDTO.getPrice());
        }
        if (updateGameRequestDTO.getRecommendation() != null) {
            targetGame.setRecommendation(updateGameRequestDTO.getRecommendation());
        }
        if (updateGameRequestDTO.getReleaseDate() != null) {
            targetGame.setReleaseDate(updateGameRequestDTO.getReleaseDate());
        }
        if (updateGameRequestDTO.getPositive() != null) {
            targetGame.setPositive(updateGameRequestDTO.getPositive());
        }
        if (updateGameRequestDTO.getKoIsPosible() != null) {
            targetGame.setKoIsPosible(updateGameRequestDTO.getKoIsPosible());
        }
        if (updateGameRequestDTO.getEnIsPosible() != null) {
            targetGame.setEnIsPosible(updateGameRequestDTO.getEnIsPosible());
        }
        if (updateGameRequestDTO.getJpIsPosible() != null) {
            targetGame.setJpIsPosible(updateGameRequestDTO.getJpIsPosible());
        }
        if (updateGameRequestDTO.getDeveloper() != null) {
            targetGame.setDeveloper(updateGameRequestDTO.getDeveloper());
        }
        if (updateGameRequestDTO.getPublisher() != null) {
            targetGame.setPublisher(updateGameRequestDTO.getPublisher());
        }

        List<GameCategory> targetGameCategoryList = targetGame.getGameCategoryList();
        List<UpdateGameCategoryDTO> updateGameCategoryDTOList = updateGameRequestDTO.getUpdateGameCategoryDTOList();

        // Set으로 변환하여 빠르게 비교할 수 있도록 함
        Set<Long> existingCategoryIds = targetGameCategoryList.stream()
                .map(gameCategory -> gameCategory.getCategory().getId())
                .collect(Collectors.toSet());

        Set<Long> newCategoryIds = updateGameCategoryDTOList.stream()
                .map(UpdateGameCategoryDTO::getId)
                .collect(Collectors.toSet());

        // 삭제할 카테고리 (existing - new)
        Set<Long> toRemove = new HashSet<>(existingCategoryIds);
        toRemove.removeAll(newCategoryIds);

        // 추가할 카테고리 (new - existing)
        Set<Long> toAdd = new HashSet<>(newCategoryIds);
        toAdd.removeAll(existingCategoryIds);

        // 기존 카테고리 리스트에서 삭제할 항목 제거 및 삭제
        Iterator<GameCategory> iterator = targetGameCategoryList.iterator();
        while (iterator.hasNext()) {
            GameCategory gameCategory = iterator.next();
            if (toRemove.contains(gameCategory.getCategory().getId())) {
                gameCategoryRepository.delete(gameCategory);
                iterator.remove();
            }
        }

        // 추가할 항목 추가 및 저장
        for (UpdateGameCategoryDTO dto : updateGameCategoryDTOList) {
            if (toAdd.contains(dto.getId())) {
                Category category = new Category();
                category.setId(dto.getId());
                category.setName(dto.getName());

                GameCategory newCategory = GameCategory.builder()
                        .game(targetGame)
                        .category(category)
                        .build();

                gameCategoryRepository.save(newCategory); // DB에 저장
                targetGameCategoryList.add(newCategory);
            }
        }

        targetGame.setGameCategoryList(targetGameCategoryList);  // 변경된 리스트를 다시 설정

        gameRepository.save(targetGame);
        return true;
    }



    public boolean deleteGameDetail(Integer appId) {
        if (gameRepository.deleteByAppId(appId) == 1) {
            return true;
        }
        return false;
    }


    public Page<GetGameDetailDTO> getSearchGameDTO(SearchGameRequestDTO dto) {
        int size = dto.getSize();
        int page = dto.getPage()-1;
        String keyword = dto.getKeyword();
        List<Long> categoryIds  = dto.getCategoryIds();
        Integer minPrice = dto.getMinPrice();
        Integer maxPrice = dto.getMaxPrice();
        boolean isFree = dto.getIsFree();
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();

        Sort sort = Sort.unsorted();

        if (dto.getNameSortDirection() != null) {
            sort = sort.and(Sort.by(Sort.Direction.valueOf(dto.getNameSortDirection()), "name"));
        }
        if (dto.getPriceSortDirection() != null) {
            sort = sort.and(Sort.by(Sort.Direction.valueOf(dto.getPriceSortDirection()), "price"));
        }
        if (dto.getRecommendationSortDirection() != null) {
            sort = sort.and(Sort.by(Sort.Direction.valueOf(dto.getRecommendationSortDirection()), "recommendation"));
        }
        if (dto.getDateSortDirection() != null) {
            sort = sort.and(Sort.by(Sort.Direction.valueOf(dto.getDateSortDirection()), "releaseDate"));
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Game> gameList = gameQueryRepository.searchGame(keyword, categoryIds, minPrice, maxPrice, isFree, startDate, endDate, pageable);

        List<GetGameDetailDTO> getSearchGameDTOList = new ArrayList<>();

        for (Game game : gameList) {
            GetGameDetailDTO searchGameDTO = mapGameToDTO(game);
            getSearchGameDTOList.add(searchGameDTO);
        }
        if (getSearchGameDTOList.isEmpty()) {
            throw new CustomException(ErrorCodeEnum.NOT_FOUND_GAME);
        }
        return new PageImpl<>(getSearchGameDTOList, PageRequest.of(page, size), getSearchGameDTOList.size());

    }

    public Page<GetGameDetailDTO> getReleaseGameDTO(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "releaseDate");

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Game> gameList = gameQueryRepository.gameList(pageable);

        List<GetGameDetailDTO> getGameListDTOList = new ArrayList<>();

        for (Game game : gameList) {
            GetGameDetailDTO gameReleaseDTO = mapGameToDTO(game);
            getGameListDTOList.add(gameReleaseDTO);
        }

        if (getGameListDTOList.isEmpty()) {
            throw new CustomException(ErrorCodeEnum.NOT_FOUND_GAME);
        }

        return new PageImpl<>(getGameListDTOList, PageRequest.of(page, size), getGameListDTOList.size());
    }

    public Page<GetGameDetailDTO> getRecomandGameDTO(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "recommendation");

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Game> gameList = gameQueryRepository.gameList(pageable);

        List<GetGameDetailDTO> getGameListDTOList = new ArrayList<>();

        for (Game game : gameList) {
            GetGameDetailDTO gameReleaseDTO = mapGameToDTO(game);
            getGameListDTOList.add(gameReleaseDTO);
        }

        if (getGameListDTOList.isEmpty()) {
            throw new CustomException(ErrorCodeEnum.NOT_FOUND_GAME);
        }

        return new PageImpl<>(getGameListDTOList, PageRequest.of(page, size), getGameListDTOList.size());
    }

    public Page<GetGameDetailDTO> getPositiveGameDTO(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "recommendation");

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Game> gameList = gameQueryRepository.gamePositiveList(pageable);

        List<GetGameDetailDTO> getGameListDTOList = new ArrayList<>();

        for (Game game : gameList) {
            GetGameDetailDTO gameReleaseDTO = mapGameToDTO(game);
            getGameListDTOList.add(gameReleaseDTO);
        }

        if (getGameListDTOList.isEmpty()) {
            throw new CustomException(ErrorCodeEnum.NOT_FOUND_GAME);
        }

        return new PageImpl<>(getGameListDTOList, PageRequest.of(page, size), getGameListDTOList.size());
    }

    public Page<GetGameDetailDTO> getLikeGameDTO(int page, int size, CustomUserDetails userInfo) {
        //토큰에서 username 빼내기
        String username = userInfo.getUsername();
        Member member = memberService.findMemberByUsername(username);

        List<Article> articleList = member.getArticles();
        List<Game> gameList = new ArrayList<>();

        for (Article article : articleList) {
            if (article.getHeader() == Header.REVIEW) {
                gameList.add(gameRepository.findByAppId(article.getAppId().intValue()));
            }
        }

        List<GetGameDetailDTO> getGameListDTOList = new ArrayList<>();

        for (Game game : gameList) {
            GetGameDetailDTO gameReleaseDTO = mapGameToDTO(game);
            getGameListDTOList.add(gameReleaseDTO);
        }

        if (getGameListDTOList.isEmpty()) {
            throw new CustomException(ErrorCodeEnum.NOT_FOUND_GAME);
        }

        int start = page * size;
        int end = Math.min(start + size, getGameListDTOList.size());

        List<GetGameDetailDTO> pageList = getGameListDTOList.subList(start, end);

        return new PageImpl<>(pageList, PageRequest.of(page, size), pageList.size());
    }

    public Page<GetGameDetailDTO> getCategoryGameDTO(int page, int size, Long id) {

        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        Pageable pageable = PageRequest.of(page, size, sort);

        List<Long> categoryId = new ArrayList<>();
        categoryId.add(id);

        Page<Game> gameList = gameQueryRepository.categoryGame(categoryId,pageable);

        List<GetGameDetailDTO> getGameListDTOList = new ArrayList<>();

        for (Game game : gameList) {
            GetGameDetailDTO gameReleaseDTO = mapGameToDTO(game);
            getGameListDTOList.add(gameReleaseDTO);
        }

        if (getGameListDTOList.isEmpty()) {
            throw new CustomException(ErrorCodeEnum.NOT_FOUND_GAME);
        }

        return new PageImpl<>(getGameListDTOList, PageRequest.of(page, size), getGameListDTOList.size());
    }

    public long getGameCount() {
        return gameRepository.count();
    }

    private GetGameDetailDTO mapGameToDTO(Game game) {
        List<Category> categoryList = game.getGameCategoryList().stream()
                .map(GameCategory::getCategory)
                .collect(Collectors.toList());

        List<GetGameCategoryDTO> getGameCategoryDTOList = categoryList.stream()
                .map(category -> new GetGameCategoryDTO(category.getId(), category.getName()))
                .collect(Collectors.toList());

        List<GetGameScreenshotDTO> getGameScreenshotDTOList = game.getScreenshotList().stream()
                .map(screenshot -> new GetGameScreenshotDTO(screenshot.getId(), screenshot.getThumbnailImagePath(), screenshot.getFullImagePath()))
                .collect(Collectors.toList());

        List<GetGameAchievementDTO> getGameAchievementDTOList = game.getAchievementList().stream()
                .map(achievement -> new GetGameAchievementDTO(achievement.getId(), achievement.getName(), achievement.getImagePath(), achievement.getHidden(), achievement.getDescription()))
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
                game.getPositive(),
                game.getHeaderImagePath(),
                game.getKoIsPosible(),
                game.getEnIsPosible(),
                game.getJpIsPosible(),
                getGameCategoryDTOList,
                getGameScreenshotDTOList,
                getGameAchievementDTOList
        );
    }

    public List<GetGameCategoryDTO> getCategoryList() {
        return categoryRepository.findAll().stream()
                .map(category -> new GetGameCategoryDTO(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }


}
