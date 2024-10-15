package rt.marson.syeta.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.entity.chat.Chat;
import rt.marson.syeta.entity.chat.ChatMessage;
import rt.marson.syeta.entity.chat.MessageStatus;
import rt.marson.syeta.dto.chat.ChatThumbnailDto;
import rt.marson.syeta.util.DateUtil;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatThumbnailService {
    public static final int CHATS_COUNT = 50;
    private final ChatRepoService chatRepoService;

    public List<ChatThumbnailDto> getAllChatThumbnailByUserId(Long userId) {
        return getChatsSortedByLastMessageIdDesc(userId)
                .stream()
                .map(chat -> getChatThumbnailByChat(chat, userId))
                .toList();
    }

    public ChatThumbnailDto getChatThumbnailByChat(Chat chat, Long userId) {
        User recipient = chatRepoService.getRecipient(chat.getId(), userId);
        ChatMessage message = chat.getLastMessage();
        Long lastMessageId = null;
        String lastMessageContent = null;
        OffsetDateTime lastMessageCreatedAt = null;
        if (message != null) {
            lastMessageId = message.getId();
            lastMessageContent = message.getContent();
            lastMessageCreatedAt = message.getCreatedAt();
        }

        String photoUrl = null;
        if (recipient.getPhoto() != null) {
            photoUrl = recipient.getPhoto().getUrl();
        }

        return ChatThumbnailDto.builder()
                .userId(recipient.getId())
                .photoUrl(photoUrl)
                .firstName(recipient.getFirstName())
                .lastName(recipient.getLastName())
                .lastMessageId(lastMessageId)
                .lastMessageContent(lastMessageContent)
                .lastMessageDate(DateUtil.offsetToString(lastMessageCreatedAt))
                .numberUnread(getUnreadMessageCount(chat, recipient))
                .build();
    }

    public Integer getUnreadMessageCount(Chat chat, User recipient) {
        Integer count = 0;
        if (chat.getMessages() != null && !chat.getMessages().isEmpty()
            && chat.getLastMessage().getSender().getId().equals(recipient.getId())) {
            for (int i = chat.getMessages().size() - 1; i >= 0; i--) {
                ChatMessage message = chat.getMessages().get(i);
                if (message.getSender().equals(recipient) && message.getStatus().equals(MessageStatus.SENT)) {
                    count++;
                } else {
                    break;
                }
            }
        }
        return count;
    }

    public List<Chat> getChatsSortedByLastMessageIdDesc(Long userId) {
        Pageable pageable = PageRequest.of(0, CHATS_COUNT);
        return chatRepoService.getAllByUserIdOrderByLastMessageIdDesc(userId, pageable);
    }
}
