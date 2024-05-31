package mytrophy.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // endpoint : /api/chat/{roomId}
        // 이를 통해 ws://localhost:8080/api/chat/{roomId}로 접속 가능
        // setAllowedOrigins("*") : 모든 도메인에서 접근 가능
        registry.addHandler(webSocketHandler, "/api/chat/{roomId}")
                .setAllowedOrigins("*");
    }
}
