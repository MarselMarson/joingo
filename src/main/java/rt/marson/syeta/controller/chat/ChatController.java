package rt.marson.syeta.controller.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import rt.marson.syeta.dto.auth.AuthUserId;
import rt.marson.syeta.dto.chat.ChatDto;
import rt.marson.syeta.dto.chat.ChatMessageDto;
import rt.marson.syeta.dto.chat.ChatThumbnailDto;
import rt.marson.syeta.dto.chat.UnreadChatDto;
import rt.marson.syeta.service.chat.ChatService;
import rt.marson.syeta.service.chat.ChatThumbnailService;
import rt.marson.syeta.service.chat.UnreadChatService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final ChatThumbnailService thumbnailService;
    private final UnreadChatService unreadChatService;

    @GetMapping("/{chatId}")
    public ChatDto getChatById(@AuthUserId Long senderId, @PathVariable(name = "chatId") Long chatId) {
        return chatService.getChatById(senderId, chatId);
    }

    @GetMapping("/user/{recipientId}")
    public List<ChatMessageDto> openChat(@AuthUserId Long senderId, @PathVariable(name = "recipientId") Long recipientId) {
        return chatService.openChat(senderId, recipientId);
    }

    @GetMapping("/userChats")
    public List<ChatThumbnailDto> getAllChatThumbnail(@AuthUserId Long userId) {
        return thumbnailService.getAllChatThumbnailByUserId(userId);
    }

    @DeleteMapping("/{chatId}/messages")
    public void deleteAllMessages(@AuthUserId Long userId, @PathVariable(name = "chatId") Long chatId) {
        chatService.deleteChatMessages(chatId, userId);
    }

    @GetMapping("/unreadChats")
    public Set<UnreadChatDto> getUnreadChats(@AuthUserId Long userId) {
        return unreadChatService.getUnreadChatsDto(userId);
    }
}
