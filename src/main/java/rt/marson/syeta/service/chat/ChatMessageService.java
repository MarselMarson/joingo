package rt.marson.syeta.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.entity.chat.Chat;
import rt.marson.syeta.entity.chat.ChatMessage;
import rt.marson.syeta.entity.chat.MessageStatus;
import rt.marson.syeta.dto.chat.ChatMessageDto;
import rt.marson.syeta.mapper.chat.ChatMessageMapper;
import rt.marson.syeta.service.user.UserService;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final UserService userService;
    private final ChatMessageRepoService messageRepoService;

    private final ChatMessageMapper messageMapper;

    @Transactional
    public ChatMessage saveMessage(ChatMessageDto chatMessage, Chat chat) {
        User sender = userService.findUserById(chatMessage.getSenderId());
        User recipient;

        if (!chat.getUser1().getId().equals(sender.getId())) {
            recipient = chat.getUser1();
            if (!chat.getUser2().getId().equals(sender.getId())) {
                throw new AccessDeniedException("User with id: " + sender.getId() +
                                                " has no access to the chat id: " + chat.getId());
            }
        } else {
            recipient = chat.getUser2();
        }

        ChatMessage message = ChatMessage.builder()
                .chat(chat)
                .sender(sender)
                .recipient(recipient)
                .content(chatMessage.getContent())
                .status(MessageStatus.SENT)
                .build();
        messageRepoService.saveMessage(message);

        return message;
    }

    public ChatMessageDto toDto(ChatMessage message) {
        return messageMapper.toDto(message);
    }

    @Transactional
    public void setMessagesStatusToDelivered(Chat chat, Long recipientId) {
        if (chat.getMessages() != null && !chat.getMessages().isEmpty()) {
            setMessagesStatusToDelivered(chat, chat.getMessages().size() - 1, recipientId);
        }
    }

    public void setMessagesStatusToDelivered(Chat chat, int messageIndex, Long senderId) {
        for (int i = messageIndex; i >= 0; i--) {
            ChatMessage message = chat.getMessages().get(i);
            Long messageSenderId = message.getSender().getId();
            if (messageSenderId.equals(senderId)) {
                if (message.getStatus().equals(MessageStatus.DELIVERED)) {
                    break;
                }
                message.setStatus(MessageStatus.DELIVERED);
                messageRepoService.saveMessage(message);
            }
        }
    }

    @Transactional
    public void setMessageStatusToDelivered(ChatMessageDto chatMessageDto, Chat chat) {
        User user = userService.findUserById(chatMessageDto.getSenderId());
        ChatMessage targetMessage = messageRepoService.getMessageById(chatMessageDto.getId());
        Long senderId = targetMessage.getSender().getId();

        if (!senderId.equals(user.getId())
            && (user.getId().equals(chat.getUser1().getId()) || user.getId().equals(chat.getUser2().getId()))) {
            int messageIndex = getMessageIndex(chat, targetMessage);

            setMessagesStatusToDelivered(chat, messageIndex, senderId);
        } else {
            throw new RuntimeException();
        }
    }

    private static int getMessageIndex(Chat chat, ChatMessage targetMessage) {
        int messageIndex = -1;
        for (int i = chat.getMessages().size() - 1; i >= 0; i--) {
            if (chat.getMessages().get(i).getId().equals(targetMessage.getId())) {
                messageIndex = i;
            }
        }
        return messageIndex;
    }
}
