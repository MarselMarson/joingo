package rt.marson.syeta.service.chat;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.entity.chat.Chat;
import rt.marson.syeta.repository.chat.ChatRepo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatRepoService {
    private final ChatRepo chatRepo;

    public Chat getChatById(Long id) {
        return chatRepo.findById(id).orElseThrow(
                () -> new EntityNotFoundException("chat with id: " + id + " not found"));
    }

    public User getRecipient(Long chatId, Long senderId) {
        Chat chat = getChatById(chatId);
        return chat.getUser1().getId().equals(senderId) ? chat.getUser2() : chat.getUser1();
    }

    public List<Chat> getAllByUserIdOrderByLastMessageIdDesc(Long userId, Pageable pageable) {
        return chatRepo.findAllByUserIdOrderByLastMessageIdDesc(userId, pageable);
    }

    public Chat save(Chat chat) {
        return chatRepo.save(chat);
    }

    public Optional<Chat> findByUser1IdAndUser2Id(Long user1Id, Long user2Id) {
        return chatRepo.findByUser1IdAndUser2Id(user1Id, user2Id);
    }

    public Set<Chat> findAllUnreadChatsByUserId(Long userId) {
        return chatRepo.findAllUnreadChatsByUserId(userId);
    }
}
