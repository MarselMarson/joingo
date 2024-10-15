package rt.marson.syeta.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import rt.marson.syeta.entity.chat.ChatMessage;

@Repository
public interface ChatMessageRepo extends JpaRepository<ChatMessage, Long>, JpaSpecificationExecutor<ChatMessage> {
}
