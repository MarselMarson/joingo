package rt.marson.syeta.config.websocket.handler;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.DependsOn;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import rt.marson.syeta.config.websocket.handler.operation.GlobalWebSocketOperations;
import rt.marson.syeta.dto.chat.ChatMessageDto;
import rt.marson.syeta.dto.chat.ChatThumbnailDto;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.entity.chat.Chat;
import rt.marson.syeta.entity.chat.ChatMessage;
import rt.marson.syeta.security.JwtService;
import rt.marson.syeta.service.chat.ChatMessageService;
import rt.marson.syeta.service.chat.ChatRepoService;
import rt.marson.syeta.service.chat.ChatService;
import rt.marson.syeta.service.chat.ChatThumbnailService;
import rt.marson.syeta.service.user.UserService;
import rt.marson.syeta.util.DateUtil;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@DependsOn("globalWebSocketOperations")
public class ChatWebSocketHandler extends MyWebSocketHandler {
    private final ChatService chatService;
    private final ChatRepoService chatRepoService;
    private final ChatMessageService messageService;
    private final GlobalWebSocketOperations globalWebSocketOperations;
    private final ChatsWebSocketHandler chatsHandler;
    private final ChatThumbnailService thumbnailService;

    public ChatWebSocketHandler(JwtService jwtService, UserService userService, ChatService chatService, ChatMessageService messageService, GlobalWebSocketOperations globalWebSocketOperations, ChatRepoService chatRepoService, ChatsWebSocketHandler chatsHandler, ChatThumbnailService thumbnailService) {
        super(jwtService, userService);
        this.chatService = chatService;
        this.messageService = messageService;
        this.globalWebSocketOperations = globalWebSocketOperations;
        this.chatRepoService = chatRepoService;
        this.chatsHandler = chatsHandler;
        this.thumbnailService = thumbnailService;
    }

    @Override
    @Transactional
    protected void handleTextMessage(@NonNull WebSocketSession session, @NotNull TextMessage message) throws Exception {
        if (getSessionInfo().containsKey(session)) {
            sendMessage(session, message);
        } else {
            authUser(session, message);
        }
    }

    @Override
    public void sendMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            SessionInfo si = getSessionInfo().get(session);
            Long senderId = si.getUserId();
            Long recipientId = si.getRecipientId();
            ChatMessageDto chatMessage = objectMapper.readValue(message.getPayload(), ChatMessageDto.class);
            chatMessage.setSenderId(senderId);
            chatMessage.setChatPartnerId(recipientId);
            Chat chat = chatService.getChatByUsers(senderId, recipientId).get();

            System.out.println();
            System.out.println("user id: " + senderId + " sending message to id: " + recipientId);
            System.out.println();

            //Если есть id - подтверждение доставки
            if (chatMessage.getId() != null) {
                messageService.setMessageStatusToDelivered(chatMessage, chat);
                //получатель - отправитель подтверждения
                globalWebSocketOperations.removeUnreadChat(senderId, chat.getId());
            } else {
                ChatMessage savedMessage = messageService.saveMessage(chatMessage, chat);
                chatService.addMessage(chat, savedMessage);
                chatService.updateChatLastMessage(chat, savedMessage);
                ChatMessageDto savedMessageDto = messageService.toDto(savedMessage);
                savedMessageDto.setSentId(chatMessage.getSentId());

                User recipient = chatRepoService.getRecipient(chat.getId(), senderId);
                TextMessage textMessage = getTextMessage(savedMessageDto);

                session.sendMessage(textMessage);
                if (getUserSessions().containsKey(recipient.getId())) {
                    WebSocketSession recipientSession = getUserSessions().get(recipient.getId());
                    Long recipientChatId = getSessionInfo().get(recipientSession).getChatId();
                    if (chat.getId().equals(recipientChatId)) {
                        recipientSession.sendMessage(textMessage);
                    }
                }

                if (!getUserSessions().containsKey(recipient.getId())) {
                    newMessageHandler(recipient.getId(), chat);
                    globalWebSocketOperations.addUnreadChat(recipient.getId(), chat.getId());
                }

                System.out.println();
                System.out.println("user id: " + senderId + " sent message to id: " + recipientId);
                System.out.println();

                System.out.println("User id: " + senderId +
                                   " send message: " + chatMessage.getContent() +
                                   " to chat id: " + chat.getId());
            }
        } catch (RuntimeException e) {
            handleTransportError(session, e);
        }
    }

    @Override
    public void authUser(WebSocketSession session, TextMessage message) throws Exception {
        AuthMessage auth = objectMapper.readValue(message.getPayload(), AuthMessage.class);

        String jwt = auth.getJwt();
        Long chatPartnerId = auth.getChatPartnerId();

        if (jwt == null || jwt.isEmpty() || chatPartnerId == null) {
            handleTransportErrorAndClose(session, new AccessDeniedException("jwt or chatPartnerId don't set correct"));
        } else {
            String username = jwtService.extractUserName(jwt);
            UserDetails userDetails = userService
                    .userDetailsService()
                    .loadUserByUsername(username);
            User user = userService.getUserByEmail(username);

            System.out.println();
            System.out.println("user id: " + user.getId() + " connecting chat with id: " + chatPartnerId);
            System.out.println();

            Chat chat = chatService.getChatOrCreate(user.getId(), chatPartnerId);

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

                Long recipientId = user.getId().equals(chat.getUser1().getId())
                        ? chat.getUser2().getId() : chat.getUser1().getId();

                getSessionInfo().put(session, new SessionInfo(user.getId(), chat.getId(), recipientId));
                getUserSessions().put(user.getId(), session);

                session.sendMessage(getTextMessage(new AuthAnswer(true)));
                messageService.setMessagesStatusToDelivered(chat, recipientId);
                newMessageHandler(user.getId(), chat);
                globalWebSocketOperations.removeUnreadChat(user.getId() , chat.getId());

                System.out.println();
                System.out.println("user id: " + user.getId() + " connected chat with id: " + chatPartnerId);
                System.out.println();
            } else {
                handleTransportErrorAndClose(session,
                        new AccessDeniedException("Invalid JWT or user id: " +
                                                  user.getId() +
                                                  " have no access to chat id: " + chat.getId()));
            }
        }
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        try {
            TextMessage errorMessage = new TextMessage(
                    objectMapper.writeValueAsString(ChatMessageDto.builder()
                            .chatPartnerId(0L)
                            .content(exception.getMessage())
                            .createdDate(DateUtil.localToString(LocalDateTime.now()))
                            .build()));
            session.sendMessage(errorMessage);
        } catch (RuntimeException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        super.afterConnectionClosed(session, status);
        System.out.println("User disconnected: " + session.getId());
    }

    public void newMessageHandler(Long recipientId, Chat chat) throws IOException {
        WebSocketSession recipientSession = chatsHandler.getUserSessions().get(recipientId);
        if (recipientSession != null) {
            ChatThumbnailDto answer = thumbnailService.getChatThumbnailByChat(chat, recipientId);
            recipientSession.sendMessage(chatsHandler.getTextMessage(answer));
        }
    }
}
