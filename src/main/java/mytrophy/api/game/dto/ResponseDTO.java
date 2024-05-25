package mytrophy.api.game.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class ResponseDTO {

    @Data
    @AllArgsConstructor
    public static class GetAllGameDTO{
        private Long id;
        private String name;
        private String headerImagePath;
    }

    @Data
    @AllArgsConstructor
    public static class GetGameDetailDTO {
        private Long id;
        private String name;
        private String description;
        private String developer;
        private String publisher;
        private String requirement;
        private Integer price;
        private String releaseDate;
        private Integer recommendation;
        private String headerImagePath;
        private Boolean koIsPosible;
        private Boolean enIsPosible;
        private Boolean jpIsPosible;
        private List<GetGameCategoryDTO> getGameCategoryDTOList;
        private List<GetGameScreenshotDTO> getGameScreenshotDTOList;
        private List<GetGameAchievementDTO> getGameAchievementDTOList;
    }

    @Data
    @AllArgsConstructor
    public static class GetSearchGameDTO{
        private Long id;
        private String name;
        private String headerImagePath;
    }


    @Data
    @AllArgsConstructor
    public static class GetGameCategoryDTO {
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    public static class GetGameAchievementDTO {
        private Long id;
        private String name;
        private String imagePath;
    }

    @Data
    @AllArgsConstructor
    public static class GetGameScreenshotDTO {
        private Long id;
        private String thumbnailImagePath;
        private String fullImagePath;
    }

}
