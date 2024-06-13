package mytrophy.api.game.service;

import lombok.RequiredArgsConstructor;
import mytrophy.api.game.dto.ResponseDTO;
import mytrophy.api.game.entity.Category;
import mytrophy.api.game.entity.Game;
import mytrophy.api.game.entity.GameCategory;
import mytrophy.api.game.entity.GameReview;
import mytrophy.api.game.enums.ReviewStatus;
import mytrophy.api.game.repository.GameRepository;
import mytrophy.api.game.repository.GameReviewRepository;
import mytrophy.api.member.entity.Member;
import mytrophy.api.member.entity.MemberCategory;
import mytrophy.api.member.repository.MemberRepository;
import mytrophy.global.handler.CustomException;
import mytrophy.global.handler.ErrorCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
public class GameRecommendService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GameRepository gameRepository;

    public Page<ResponseDTO.GetGameDetailDTO> recommendGamesForMember(Long memberId, Pageable pageable) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCodeEnum.NOT_EXISTS_MEMBER_ID));

        // 회원의 관심 카테고리 ID 목록 조회
        List<Long> interestedCategoryIds = member.getMemberCategories().stream()
                .map(MemberCategory::getCategory)
                .map(Category::getId)
                .collect(Collectors.toList());

        // 추천 게임 조회
        Page<Game> gamePage = gameRepository.findRecommendedGames(interestedCategoryIds, memberId, pageable);

        // Game -> GetGameDetailDTO 변환
        List<ResponseDTO.GetGameDetailDTO> dtoList = gamePage.getContent().stream()
                .map(this::convertToGetGameDetailDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, gamePage.getTotalElements());
    }

    private ResponseDTO.GetGameDetailDTO convertToGetGameDetailDTO(Game game) {
        List<ResponseDTO.GetGameCategoryDTO> categoryDTOs = game.getGameCategoryList().stream()
                .map(gc -> new ResponseDTO.GetGameCategoryDTO(gc.getCategory().getId(), gc.getCategory().getName()))
                .collect(Collectors.toList());

        List<ResponseDTO.GetGameScreenshotDTO> screenshotDTOs = game.getScreenshotList().stream()
                .map(gs -> new ResponseDTO.GetGameScreenshotDTO(gs.getId(), gs.getThumbnailImagePath(), gs.getFullImagePath()))
                .collect(Collectors.toList());

        List<ResponseDTO.GetGameAchievementDTO> achievementDTOs = game.getAchievementList().stream()
                .map(ga -> new ResponseDTO.GetGameAchievementDTO(ga.getId(), ga.getName(), ga.getImagePath(), ga.getHidden(), ga.getDescription()))
                .collect(Collectors.toList());

        return new ResponseDTO.GetGameDetailDTO(
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
                categoryDTOs,
                screenshotDTOs,
                achievementDTOs
        );
    }
}
