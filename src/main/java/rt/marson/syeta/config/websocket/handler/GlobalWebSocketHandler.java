package rt.marson.syeta.config.websocket.handler;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.entity.chat.Chat;
import rt.marson.syeta.security.JwtService;
import rt.marson.syeta.service.chat.UnreadChatService;
import rt.marson.syeta.service.event.UserEventNotificationsService;
import rt.marson.syeta.service.user.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class GlobalWebSocketHandler extends MyWebSocketHandler {
    @Getter
    private final Map<Long, Set<Long>> userUnreadChats;
    @Getter
    private final Map<Long, Set<Long>> userNotifiedEvents;
    private final UnreadChatService unreadChatService;
    private final UserEventNotificationsService notificationService;

    public GlobalWebSocketHandler(JwtService jwtService, UserService userService, UnreadChatService unreadChatService, UserEventNotificationsService notificationService) {
        super(jwtService, userService);
        this.unreadChatService = unreadChatService;
        this.notificationService = notificationService;
        this.userUnreadChats = new ConcurrentHashMap<>();
        this.userNotifiedEvents = new ConcurrentHashMap<>();
    }

    @Override
    @Transactional
    protected void handleTextMessage(@NonNull WebSocketSession session, @NotNull TextMessage message) throws Exception {
        authUser(session, message);
    }

    private void sendAnswer(WebSocketSession session, Long userId) throws IOException {
        Set<Long> unreadChats = unreadChatService.getUnreadChats(userId).stream()
                .map(Chat::getId).collect(Collectors.toCollection(ConcurrentHashMap::newKeySet));
        getUserUnreadChats().put(userId, unreadChats);

        Set<Long> notifiedEventsId = notificationService.getNotificationEventsId(userId);
        getUserNotifiedEvents().put(userId, notifiedEventsId);

        List<GlobalAnswer> answers = new ArrayList<>();
        answers.add(new GlobalAnswer(GlobalSocketMessageTypes.UNREAD_CHATS.name(), unreadChats.size()));
        answers.add(new GlobalAnswer(GlobalSocketMessageTypes.EVENT_NOTIFICATIONS.name(), notifiedEventsId.size()));

        System.out.println(getUserUnreadChats());
        System.out.println(getUserNotifiedEvents());
        session.sendMessage(getTextMessage(answers));
    }

    @Override
    public void authUser(WebSocketSession session, TextMessage message) throws Exception {
        AuthMessage auth = objectMapper.readValue(message.getPayload(), AuthMessage.class);
        String jwt = auth.getJwt();

        if (jwt == null || jwt.isEmpty()) {
            session.close();
        } else {
            String username = jwtService.extractUserName(jwt);
            UserDetails userDetails = userService
                    .userDetailsService()
                    .loadUserByUsername(username);
            User user = userService.getUserByEmail(username);


            System.out.println();
            System.out.println("user id: " + user.getId() + " connecting to global");
            System.out.println();

            if (jwtService.isTokenValid(jwt, userDetails)) {

                SecurityContext context = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);

                if (getUserSessions().containsKey(user.getId())) {
                    WebSocketSession userSession = getUserSessions().get(user.getId());
                    if (userSession.isOpen()) {
                        userSession.close();
                    }
                }

                getSessionInfo().put(session, new SessionInfo(user.getId()));
                getUserSessions().put(user.getId(), session);

                sendAnswer(session, user.getId());

                System.out.println();
                System.out.println("user id: " + user.getId() + " connected to global");
                System.out.println();
            } else {
                session.close();
            }
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        if (getSessionInfo().containsKey(session)) {
            Long userId = getSessionInfo().get(session).getUserId();
            getUserSessions().remove(userId);
            getUserUnreadChats().remove(userId);
            getUserNotifiedEvents().remove(userId);
            System.out.println("User id: " + userId + " disconnected global");
        }
        System.out.println("User disconnected global");
        getSessionInfo().remove(session);
    }
}
