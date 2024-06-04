package mytrophy.api.game.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
public class ResponseDTO {

    @Data
    @AllArgsConstructor
    public static class GetAllGameDTO{
        private Integer id;
        private String name;
        private String headerImagePath;
        private Integer price;
        private Boolean koPossible;
        private Boolean enPossible;
        private Boolean jpPossible;
        private List<GetGameCategoryDTO> categoryList;
    }

    @Data
    @AllArgsConstructor
    public static class GetGameDetailDTO {
        private Integer id;
        private String name;
        private String description;
        private String developer;
        private String publisher;
        private String requirement;
        private Integer price;
        private LocalDate releaseDate;
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
        private Integer id;
        private String name;
        private String headerImagePath;
        private Integer price;
        private Boolean koPossible;
        private Boolean enPossible;
        private Boolean jpPossible;
        private List<GetGameCategoryDTO> getGameCategoryDTOList;
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
        private Boolean hidden;
        private String description;
    }

    @Data
    @AllArgsConstructor
    public static class GetGameScreenshotDTO {
        private Long id;
        private String thumbnailImagePath;
        private String fullImagePath;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetTopGameDTO{
        private Integer id;
        private String name;
        private String description;
        private String developer;
        private String publisher;
        private String requirement;
        private Integer price;
        private LocalDate releaseDate;
        private Integer recommendation;
        private String headerImagePath;
        private Boolean koIsPosible;
        private Boolean enIsPosible;
        private Boolean jpIsPosible;
        private List<GetGameCategoryDTO> getGameCategoryDTOList;
        private List<GetGameScreenshotDTO> getGameScreenshotDTOList;
        private List<GetGameAchievementDTO> getGameAchievementDTOList;
        private Integer rank;
    }

    @Data
    @AllArgsConstructor
    public static class GetGameListDTO {
        private Integer id;
        private String name;
        private String description;
        private String developer;
        private String publisher;
        private String requirement;
        private Integer price;
        private LocalDate releaseDate;
        private Integer recommendation;
        private String headerImagePath;
        private Boolean koIsPosible;
        private Boolean enIsPosible;
        private Boolean jpIsPosible;
        private List<GetGameCategoryDTO> getGameCategoryDTOList;
        private List<GetGameScreenshotDTO> getGameScreenshotDTOList;
        private List<GetGameAchievementDTO> getGameAchievementDTOList;
    }

}
