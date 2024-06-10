package mytrophy.api.game.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mytrophy.api.game.enums.Positive;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RequestDTO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SearchGameRequestDTO {

        private int page = 1;
        private int size = 10;
        private String keyword = "";
        private List<Long> categoryIds = new ArrayList<>();
        private Integer minPrice = null;
        private Integer maxPrice = null;
        private Boolean isFree = false;
        private LocalDate startDate = null;
        private LocalDate endDate = null;
        private String priceSortDirection = null;
        private String recommendationSortDirection = null;
        private String nameSortDirection = null;
        private String dateSortDirection = null;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateGameRequestDTO {
        private String name;
        private String description;
        private String developer;
        private String publisher;
        private String requirement;
        private Integer price;
        private LocalDate releaseDate;
        private Integer recommendation;
        private Positive positive;
        private Boolean koIsPosible;
        private Boolean enIsPosible;
        private Boolean jpIsPosible;
        private List<UpdateGameCategoryDTO> updateGameCategoryDTOList;
    }

    @Data
    @AllArgsConstructor
    public static class UpdateGameCategoryDTO {
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateGameReviewDto {
        private String reviewStatus;
    }
}
