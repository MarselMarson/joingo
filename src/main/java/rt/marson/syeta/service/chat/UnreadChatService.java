package rt.marson.syeta.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rt.marson.syeta.entity.chat.Chat;
import rt.marson.syeta.dto.chat.UnreadChatDto;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnreadChatService {
    private final ChatRepoService chatRepoService;

    public Set<Chat> getUnreadChats(Long userId) {
        return chatRepoService.findAllUnreadChatsByUserId(userId);
    }

    public Set<UnreadChatDto> getUnreadChatsDto(Long userId) {
        return getUnreadChats(userId).stream()
                .map(this::toUnreadChat)
                .collect(Collectors.toSet());
    }

    public UnreadChatDto toUnreadChat(Chat chat) {
        return new UnreadChatDto(chat.getId(), true);
    }
}
