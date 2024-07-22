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
        private List<Long> categoryIds;
        private Integer minPrice;
        private Integer maxPrice;
        private Boolean isFree = false;
        private LocalDate startDate;
        private LocalDate endDate;
        private String priceSortDirection;
        private String recommendationSortDirection;
        private String nameSortDirection;
        private String dateSortDirection;
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
        private Boolean koIsPossible;
        private Boolean enIsPossible;
        private Boolean jpIsPossible;
        private List<UpdateGameCategoryDTO> updateGameCategoryDTOList;
        private String averageEmbeddingVector;
    }

    @Data
    @AllArgsConstructor
    public static class UpdateGameCategoryDTO {
        private Long id;
        private String name;
        private String embeddingVector;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateGameReviewDto {
        private String reviewStatus;
    }
}
