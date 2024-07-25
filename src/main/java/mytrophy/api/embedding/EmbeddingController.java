package mytrophy.api.embedding;

import mytrophy.api.game.entity.Category;
import mytrophy.api.game.repository.CategoryRepository;
import mytrophy.api.game.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/embedding")
public class EmbeddingController {

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    //특정 카테고리 임베딩 벡터 발급
    @PostMapping("/update/{categoryId}")
    public ResponseEntity<String> updateCategoryEmbedding(@PathVariable Long categoryId) {
        embeddingService.updateCategoryEmbedding(categoryId);
        return ResponseEntity.ok("임베딩 벡터 발급 완료");
    }

    //모든 카테고리 임베딩 벡터 발급
    @PostMapping("/update-all-category")
    public ResponseEntity<String> updateAllCategoryEmbeddings() {
        embeddingService.updateAllCategoryEmbeddings();
        return ResponseEntity.ok("임베딩 벡터 발급 완료");
    }

    //게임의 평균 임베딩벡터 계산
    @PostMapping("/initialize/{startId}/{batchSize}")
    public ResponseEntity<String> initializeEmbeddings(@PathVariable Long startId, @PathVariable int batchSize) {
        embeddingService.updateGameEmbeddingsInRange(startId, batchSize);
        return ResponseEntity.ok("평균 임베딩 벡터 저장 완료.");
    }

}
