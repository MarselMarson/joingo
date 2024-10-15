package rt.marson.syeta.config.websocket;

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
    private final WebSocketHandler chatWebSocketHandler;
    private final WebSocketHandler chatsWebSocketHandler;
    private final WebSocketHandler globalWebSocketHandler;
    private final WebSocketHandler eventNotificationWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/websocket")
                .setAllowedOrigins("*");

        registry.addHandler(chatsWebSocketHandler, "/websocket/chats")
                .setAllowedOrigins("*");

        registry.addHandler(globalWebSocketHandler, "/websocket/global")
                .setAllowedOrigins("*");

        registry.addHandler(eventNotificationWebSocketHandler, "/websocket/event")
                .setAllowedOrigins("*");
    }
}
