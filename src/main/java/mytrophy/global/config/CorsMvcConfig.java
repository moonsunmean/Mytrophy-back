package mytrophy.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                  // 다음 경로로 들어오는 요청에 대해
                .allowedOrigins("http://localhost:3000", "http://34.64.52.132") // 다음 경로에서 오는 요청을 허용 하며 (현재는 전부)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")   // 허용되는 메서드
                .allowedHeaders("Authorization", "Content-Type")  // 허용되는 헤드
                .exposedHeaders("Authorization", "Content-Type")  // 클라이언트측 응답에서 노출되는 헤더를 지정합니다.
                .exposedHeaders("Set-Cookie")
                .allowCredentials(true)
                .maxAge(3000); // 원하는 시간만큼 pre-flight 리퀘스트를 캐싱;
    }
}