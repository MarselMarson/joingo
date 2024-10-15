package rt.marson.syeta.service.chat;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rt.marson.syeta.entity.chat.ChatMessage;
import rt.marson.syeta.repository.chat.ChatMessageRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageRepoService {
    private final ChatMessageRepo messageRepo;

    public void saveMessage(ChatMessage message) {
        messageRepo.save(message);
    }

    public void deleteMessages(List<ChatMessage> messages) {
        messageRepo.deleteAll(messages);
    }

    public ChatMessage getMessageById(Long id) {
        return messageRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("message with id: " + id + " not found"));
    }

}
