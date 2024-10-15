package rt.marson.syeta.config.websocket.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import rt.marson.syeta.security.JwtService;
import rt.marson.syeta.service.user.UserService;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static rt.marson.syeta.config.websocket.handler.WebSocketHandlerUtil.MILLISECONDS_FOR_CHECK_IS_CONNECTED;
import static rt.marson.syeta.config.websocket.handler.WebSocketHandlerUtil.SECONDS_FOR_CONNECT_MESSAGE;

abstract public class MyWebSocketHandler extends TextWebSocketHandler {
    protected final JwtService jwtService;
    protected final UserService userService;
    protected final ObjectMapper objectMapper;

    @Getter
    private final Map<Long, WebSocketSession> userSessions;
    @Getter
    private final Map<WebSocketSession, SessionInfo> sessionInfo;

    protected MyWebSocketHandler(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
        this.userSessions = new ConcurrentHashMap<>();
        this.sessionInfo = new ConcurrentHashMap<>();
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> {
            if (sessionInfo.containsKey(session)) {
                executor.shutdown();
            }
        }, 0, MILLISECONDS_FOR_CHECK_IS_CONNECTED, TimeUnit.MILLISECONDS);

        executor.schedule(() -> {
            if (!sessionInfo.containsKey(session)) {
                try {
                    if (session.isOpen()) {
                        session.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            executor.shutdown();
        }, SECONDS_FOR_CONNECT_MESSAGE, TimeUnit.SECONDS);
    }

    public TextMessage getTextMessage(Object message) throws JsonProcessingException {
        return new TextMessage(objectMapper.writeValueAsString(message));
    }

    protected abstract void authUser(WebSocketSession session, TextMessage message) throws Exception;

    public void sendMessage(WebSocketSession session, TextMessage message) throws Exception {
        session.sendMessage(message);
    }

    public void sendObject(WebSocketSession session, Object object) throws Exception {
        sendMessage(session, getTextMessage(object));
    }

    protected void close(WebSocketSession session) throws Exception {
        session.close();
    }

    protected void handleTransportErrorAndClose(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        handleTransportError(session, exception);
        session.close();
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        if (sessionInfo.containsKey(session)) {

            Long userId = sessionInfo.get(session).getUserId();

            System.out.println();
            System.out.println("user id: " + userId + " disconnecting partner: " + sessionInfo.get(session).getRecipientId());
            System.out.println();

            userSessions.remove(userId);
        }
        sessionInfo.remove(session);
    }
}
