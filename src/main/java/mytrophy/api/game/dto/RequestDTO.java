package mytrophy.api.game.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

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
}
