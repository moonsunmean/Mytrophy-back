package mytrophy.api.game.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

public class RequestDTO {
    @Data
    @AllArgsConstructor
    public static class SearchGameRequestDTO {

        private int page = 1;
        private int size = 10;
        private String keyword = "";
        private Long categoryId = null;
        private Integer minPrice = null;
        private Integer maxPrice = null;
        private Boolean isFree = false;
        private LocalDate startDate = null;
        private LocalDate endDate = null;
        private Sort.Direction priceSortDirection = Sort.Direction.ASC;
        private Sort.Direction recommendationSortDirection = Sort.Direction.ASC;
        private Sort.Direction nameSortDirection = Sort.Direction.ASC;
        private Sort.Direction dateSortDirection = Sort.Direction.ASC;

    }
}
