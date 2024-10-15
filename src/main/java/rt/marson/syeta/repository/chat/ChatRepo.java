package rt.marson.syeta.repository.chat;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rt.marson.syeta.entity.chat.Chat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ChatRepo extends JpaRepository<Chat, Long> {
    @Query(value = "SELECT * FROM tetatet_chats WHERE user1_id = ?1 AND user2_id = ?2", nativeQuery = true)
    Optional<Chat> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);

    @Query(
            """
                SELECT c FROM Chat c
                WHERE (c.user1.id = :userId OR c.user2.id = :userId) AND c.lastMessage IS NOT NULL
                ORDER BY c.lastMessage.id desc
            """)
    List<Chat> findAllByUserIdOrderByLastMessageIdDesc(@Param("userId") Long userId, Pageable pageable);

    @Query(
            """
                SELECT c FROM Chat c
                WHERE (c.user1.id = :userId OR c.user2.id = :userId)
                    AND c.lastMessage.sender.id <> :userId
                    AND c.lastMessage.status = 'SENT'
            """)
    Set<Chat> findAllUnreadChatsByUserId(@Param("userId") Long userId);
}
