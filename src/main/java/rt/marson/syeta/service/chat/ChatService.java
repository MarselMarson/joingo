package rt.marson.syeta.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rt.marson.syeta.config.websocket.handler.operation.GlobalWebSocketOperations;
import rt.marson.syeta.dto.chat.ChatMessageDto;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.entity.chat.Chat;
import rt.marson.syeta.entity.chat.ChatMessage;
import rt.marson.syeta.dto.chat.ChatDto;
import rt.marson.syeta.mapper.chat.ChatMessageMapper;
import rt.marson.syeta.service.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {
    public static final int MESSAGES_COUNT = 100;

    private final UserService userService;
    private final ChatMessageService messageService;
    private final ChatRepoService chatRepoService;
    private final ChatMessageRepoService messageRepoService;

    private final GlobalWebSocketOperations globalWebSocketOperations;

    private final ChatMessageMapper messageMapper;

    public void updateChatLastMessage(Chat chat, ChatMessage message) {
        chat.setLastMessage(message);
        chatRepoService.save(chat);
    }

    public Optional<Chat> getChatByUsers(Long senderId, Long recipientId) {
        Long user1Id = senderId;
        Long user2Id = recipientId;
        int compareUsersId = user1Id.compareTo(user2Id);
        if (compareUsersId < 0) {
            user1Id = recipientId;
            user2Id = senderId;
        } else if (compareUsersId == 0) {
            throw new AccessDeniedException("User id: "
                    + senderId + " cannot chat with himself");
        }

        return chatRepoService.findByUser1IdAndUser2Id(user1Id, user2Id);
    }

    public Chat getChatOrCreate(Long senderId, Long recipientId) {
        User sender = userService.findUserById(senderId);
        User recipient = userService.findUserById(recipientId);

        return getChatByUsers(sender.getId(), recipient.getId())
                .orElseGet(() -> createNewChat(sender, recipient));
    }

    public Chat createNewChat(User sender, User recipient) {
        User user1 = sender;
        User user2 = recipient;
        int compareUsersId = sender.getId().compareTo(recipient.getId());
        if (compareUsersId < 0) {
            user1 = recipient;
            user2 = sender;
        } else if (compareUsersId == 0) {
            throw new AccessDeniedException("User id: "
                                            + sender.getId() + " cannot chat with himself");
        }
        Chat chat = Chat.builder()
                .user1(user1)
                .user2(user2)
                .messages(new ArrayList<>())
                .lastMessage(null)
                .active(true)
                .build();
        return chatRepoService.save(chat);
    }

    @Transactional
    public List<ChatMessageDto> openChat(Long senderId, Long recipientId) {
        Chat chat = getChatOrCreate(senderId, recipientId);
        if (chat.getMessages() != null && !chat.getMessages().isEmpty()) {
            messageService.setMessagesStatusToDelivered(chat, recipientId);
            try {
                globalWebSocketOperations.removeUnreadChat(senderId, chat.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return toChatDto(chat, userService.findUserById(recipientId)).getMessages();
    }

    public ChatDto toChatDto(Chat chat, User recipient) {
        String photoUrl = null;
        if (recipient.getPhoto() != null) {
            photoUrl = recipient.getPhoto().getUrl();
        }

        List<ChatMessage> lastMessages = getLastNMessages(chat.getMessages(), MESSAGES_COUNT);

        return ChatDto.builder()
                .firstName(recipient.getFirstName())
                .lastName(recipient.getLastName())
                .photoUrl(photoUrl)
                .userId(recipient.getId())
                .messages(lastMessages.stream()
                        .map(messageMapper::toDto)
                        .toList())
                .build();
    }

    public List<ChatMessage> getLastNMessages(List<ChatMessage> messages, int n) {
        int lastMessageIndex = messages.size() - 1;
        return lastMessageIndex > n
                ? messages.subList(lastMessageIndex - n, lastMessageIndex)
                : messages;
    }

    @Transactional
    public ChatDto getChatById(Long senderId, Long chatId) {
        Chat chat = chatRepoService.getChatById(chatId);
        User recipient;
        if (isChatForUser(chatId, senderId)) {
            if (chat.getUser1().getId().equals(senderId)) {
                recipient = chat.getUser2();
            } else {
                recipient = chat.getUser1();
            }
            messageService.setMessagesStatusToDelivered(chat, recipient.getId());
            try {
                globalWebSocketOperations.removeUnreadChat(senderId, chatId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new AccessDeniedException("Chat not for user");
        }
        return toChatDto(chat, recipient);
    }

    public boolean isChatForUser(Long chatId, Long userId) throws AccessDeniedException {
        Chat chat = chatRepoService.getChatById(chatId);
        if (!chat.getUser1().getId().equals(userId) && !chat.getUser2().getId().equals(userId)) {
            throw new AccessDeniedException("User with id: " + userId +
                                            " has no access to the chat id: " + chat.getId());
        }
        return true;
    }

    @Transactional
    public void deleteChatMessages(Long chatId, Long userId) {
        Chat chat = chatRepoService.getChatById(chatId);
        if (isChatForUser(chatId, userId)) {
            chat.setLastMessage(null);
            if (chat.getMessages() != null && !chat.getMessages().isEmpty()) {
                messageRepoService.deleteMessages(chat.getMessages());
            }
        }
    }

    public void addMessage(Chat chat, ChatMessage savedMessage) {
        chat.getMessages().add(savedMessage);
    }
}
