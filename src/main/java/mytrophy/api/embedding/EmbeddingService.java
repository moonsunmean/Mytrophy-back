package mytrophy.api.embedding;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import mytrophy.api.game.entity.Category;
import mytrophy.api.game.entity.Game;
import mytrophy.api.game.repository.CategoryRepository;
import mytrophy.api.game.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmbeddingService {

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

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public void updateAllCategoryEmbeddings() {
        List<Category> categories = categoryRepository.findAll();
        for (Category category : categories) {
            double[] embedding = fetchEmbeddingFromApi(category.getName());
            if (embedding != null) {
                try {
                    category.setEmbeddingVector(objectMapper.writeValueAsString(embedding));
                    categoryRepository.save(category);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateCategoryEmbedding(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if(categoryOptional.isPresent()) {
            Category category = categoryOptional.get();

            double[] embedding = fetchEmbeddingFromApi(category.getName());
            if (embedding != null) {
                try{
                    category.setEmbeddingVector(objectMapper.writeValueAsString(embedding));
                    categoryRepository.save(category);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("임베딩 벡터가 null입니다. 카테고리 이름: " + category.getName());
            }
        }
    }

    //카테고리에서 임베딩벡터 가져오기
    public double[] getCategoryEmbeddingById(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            if (category.getEmbeddingVector() != null) {
                try {
                    return objectMapper.readValue(category.getEmbeddingVector(), new TypeReference<double[]>() {});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("카테고리가 데이터베이스에 존재하지 않습니다. 카테고리 ID: " + categoryId);
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
                    throw new RuntimeException("임베딩 벡터 가져오기 실패");
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
                    interruptedException.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        return null;
    }

    // 여러개의 카테고리 임베딩 벡터의 평균 계산
    public double[] calculateAverageEmbedding(List<double[]> embeddings) {
        double[] avgEmbedding = new double[embeddings.get(0).length];
        for (double[] embedding : embeddings) {
            for (int i=0; i<embedding.length; i++) {
                avgEmbedding[i] += embedding[i];
            }
        }
        for (int i=0; i<avgEmbedding.length; i++) {
            avgEmbedding[i] /= embeddings.size();
        }
        return avgEmbedding;
    }

    //코사인 유사도 계산
    public double cosineSimilarity(double[] vec1, double[] vec2) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i=0; i<vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            normA += Math.pow(vec1[i], 2);
            normB += Math.pow(vec2[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    //비동기적으로 게임의 평균 임베딩 벡터 계산하고 저장
    @Async("taskExecutor")
    public void calculateAndSaveGameAverageEmbedding(Game game) {
        List<double[]> embeddings = game.getGameCategoryList().stream()
                .map(gameCategory -> {
                    Long categoryId = gameCategory.getCategory().getId();
                    double[] embedding = getCategoryEmbeddingById(categoryId);
                    if (embedding == null) {
                        System.out.println("임베딩 벡터가 null이거나 카테고리가 데이터베이스에 없습니다. 카테고리 ID: " + categoryId);
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
                e.printStackTrace();
            }
        } else {
            System.out.println("임베딩 벡터가 유효한 카테고리가 없습니다.");
        }
    }

    //모든 게임의 평균 임베딩 벡터 배치로 업데이트
    public void updateGameEmbeddingsInRange(Long startId, int batchSize) {
        Pageable pageable = PageRequest.of(0, batchSize);
        List<Game> games = gameRepository.findGamesInRange(startId, pageable);
        games.forEach(this::calculateAndSaveGameAverageEmbedding);
    }
}
