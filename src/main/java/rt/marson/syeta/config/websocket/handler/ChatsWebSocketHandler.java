package rt.marson.syeta.config.websocket.handler;

import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.security.JwtService;
import rt.marson.syeta.service.user.UserService;

@Component
public class ChatsWebSocketHandler extends MyWebSocketHandler {

    protected ChatsWebSocketHandler(JwtService jwtService, UserService userService) {
        super(jwtService, userService);
    }

    @Override
    @Transactional
    protected void handleTextMessage(@NonNull WebSocketSession session, @NotNull TextMessage message) throws Exception {
        authUser(session, message);
    }

    @Override
    protected void authUser(WebSocketSession session, TextMessage message) throws Exception {
        AuthMessage auth = objectMapper.readValue(message.getPayload(), AuthMessage.class);

        String jwt = auth.getJwt();

        if (jwt == null || jwt.isEmpty()) {
            close(session);
        } else {
            String username = jwtService.extractUserName(jwt);
            UserDetails userDetails = userService
                    .userDetailsService()
                    .loadUserByUsername(username);
            User user = userService.getUserByEmail(username);

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

                session.sendMessage(getTextMessage(new AuthAnswer(true)));
            } else {
                close(session);
            }
        }
    }
}
