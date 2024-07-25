package mytrophy.api.embedding;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import mytrophy.api.game.entity.Category;
import mytrophy.api.game.entity.Game;
import mytrophy.api.game.repository.CategoryRepository;
import mytrophy.api.game.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmbeddingService {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddingService.class);

    @Value("${clova.api.url}")
    private String apiUrl;

    @Value("${clova.api.key}")
    private String apiKey;

    @Value("${clova.api.gateway.key}")
    private String gatewayKey;

    @Value("${clova.api.request.id}")
    private String requestId;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    //모든 카테고리 임베딩벡터 발급
    public void updateAllCategoryEmbeddings() {
        List<Category> categories = categoryRepository.findAll();
        for (Category category : categories) {
            double[] embedding = fetchEmbeddingFromApi(category.getName());
            if (embedding != null) {
                try {
                    category.setEmbeddingVector(objectMapper.writeValueAsString(embedding));
                    categoryRepository.save(category);
                } catch (Exception e) {
                    logger.error("Error saving category embedding vector for category: " + category.getName(), e);
                }
            }
        }
    }

    //특정 카테고리 임베딩벡터 발급
    public void updateCategoryEmbedding(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();

            double[] embedding = fetchEmbeddingFromApi(category.getName());
            if (embedding != null) {
                try {
                    category.setEmbeddingVector(objectMapper.writeValueAsString(embedding));
                    categoryRepository.save(category);
                } catch (Exception e) {
                    logger.error("Error saving category embedding vector for category: " + category.getName(), e);
                }
            } else {
                logger.warn("Embedding vector is null for category: " + category.getName());
            }
        }
    }

    // 카테고리에서 임베딩벡터 가져오기
    public double[] getCategoryEmbeddingById(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            if (category.getEmbeddingVector() != null) {
                try {
                    return objectMapper.readValue(category.getEmbeddingVector(), new TypeReference<double[]>() {});
                } catch (Exception e) {
                    logger.error("Error reading embedding vector for category ID: " + categoryId, e);
                }
            }
        } else {
            logger.warn("Category does not exist in the database. Category ID: " + categoryId);
        }
        return null;
    }

    public double[] fetchEmbeddingFromApi(String categoryName) {
        int maxRetries = 5;
        int retryCount = 0;
        int waitTime = 2000;

        while (retryCount < maxRetries) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("X-NCP-CLOVASTUDIO-API-KEY", apiKey);
                headers.set("X-NCP-APIGW-API-KEY", gatewayKey);
                headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId);
                headers.set("Content-Type", "application/json");

                String requestBody = String.format("{\"text\": \"%s\"}", categoryName);
                HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    String responseBody = response.getBody();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    JsonNode embeddingNode = jsonNode.path("result").path("embedding");
                    double[] embedding = new double[embeddingNode.size()];
                    for (int i = 0; i < embeddingNode.size(); i++) {
                        embedding[i] = embeddingNode.get(i).asDouble();
                    }
                    return embedding;
                } else if (response.getStatusCode().value() == 429) {
                    retryCount++;
                    TimeUnit.SECONDS.sleep(5);
                } else {
                    throw new RuntimeException("Failed to fetch embedding vector");
                }
            } catch (HttpClientErrorException.TooManyRequests e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw new RuntimeException("Too many requests - rate exceeded");
                }
                try {
                    Thread.sleep(waitTime);
                    waitTime *= 2; // 대기 시간을 점진적으로 늘림
                } catch (InterruptedException interruptedException) {
                    logger.error("Interrupted while waiting to retry", interruptedException);
                }
            } catch (Exception e) {
                logger.error("Error fetching embedding from API for category: " + categoryName, e);
                break;
            }
        }
        return null;
    }

    // 여러개의 카테고리 임베딩 벡터의 평균 계산
    public double[] calculateAverageEmbedding(List<double[]> embeddings) {
        double[] avgEmbedding = new double[embeddings.get(0).length];
        for (double[] embedding : embeddings) {
            for (int i = 0; i < embedding.length; i++) {
                avgEmbedding[i] += embedding[i];
            }
        }
        for (int i = 0; i < avgEmbedding.length; i++) {
            avgEmbedding[i] /= embeddings.size();
        }
        return avgEmbedding;
    }

    //비동기적으로 게임의 평균 임베딩 벡터 계산하고 저장
    @Async("taskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void calculateAndSaveGameAverageEmbedding(Game game) {
        List<double[]> embeddings = game.getGameCategoryList().stream()
                .map(gameCategory -> {
                    Long categoryId = gameCategory.getCategory().getId();
                    double[] embedding = getCategoryEmbeddingById(categoryId);
                    if (embedding == null) {
                        logger.warn("Embedding vector is null or category does not exist in the database. Category ID: " + categoryId);
                    }
                    return embedding;
                })
                .filter(embedding -> embedding != null)
                .collect(Collectors.toList());

        if (!embeddings.isEmpty()) {
            double[] avgEmbedding = calculateAverageEmbedding(embeddings);
            try {
                game.setAverageEmbeddingVector(objectMapper.writeValueAsString(avgEmbedding));
                gameRepository.save(game);
            } catch (Exception e) {
                logger.error("Error saving game average embedding vector for game ID: " + game.getId(), e);
            }
        } else {
            logger.warn("No valid category embedding vectors found for game ID: " + game.getId());
        }
    }

    //모든 게임의 평균 임베딩 벡터 배치로 업데이트
    public void updateGameEmbeddingsInRange(Long startId, int batchSize) {
        Pageable pageable = PageRequest.of(0, batchSize);
        List<Game> games = gameRepository.findGamesInRange(startId, pageable);
        games.forEach(this::calculateAndSaveGameAverageEmbedding);
    }
}
