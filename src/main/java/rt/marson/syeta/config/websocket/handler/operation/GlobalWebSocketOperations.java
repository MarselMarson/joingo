package rt.marson.syeta.config.websocket.handler.operation;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import rt.marson.syeta.config.websocket.handler.GlobalAnswer;
import rt.marson.syeta.config.websocket.handler.GlobalSocketMessageTypes;
import rt.marson.syeta.config.websocket.handler.GlobalWebSocketHandler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@DependsOn("globalWebSocketHandler")
@RequiredArgsConstructor
public class GlobalWebSocketOperations {
    private final GlobalWebSocketHandler globalHandler;

    @Async("taskExecutor")
    public void removeUnreadChat(Long recipientId, Long chatId) throws Exception {
        System.out.println("global:" + globalHandler.getUserSessions());
        WebSocketSession recipientSession = globalHandler.getUserSessions().get(recipientId);
        if (recipientSession != null) {
            if (globalHandler.getUserUnreadChats().containsKey(recipientId)) {
                Set<Long> recipientChats = globalHandler.getUserUnreadChats().get(recipientId);
                System.out.println(recipientChats);
                if (recipientChats.remove(chatId)) {
                    GlobalAnswer answer = new GlobalAnswer(GlobalSocketMessageTypes.UNREAD_CHATS.name(), recipientChats.size());
                    globalHandler.sendObject(recipientSession, answer);
                }
            }
        }
    }

    @Async("taskExecutor")
    public void addUnreadChat(Long recipientId, Long chatId) throws Exception {
        long start = System.nanoTime();
        WebSocketSession recipientSession = globalHandler.getUserSessions().get(recipientId);
        if (recipientSession != null) {
            Set<Long> recipientChats;
            if (globalHandler.getUserUnreadChats().containsKey(recipientId)) {
                recipientChats = globalHandler.getUserUnreadChats().get(recipientId);
                if (recipientChats.add(chatId)) {
                    GlobalAnswer answer = new GlobalAnswer(GlobalSocketMessageTypes.UNREAD_CHATS.name(), recipientChats.size());
                    globalHandler.sendObject(recipientSession, answer);
                }
            } else {
                recipientChats = ConcurrentHashMap.newKeySet();
                recipientChats.add(chatId);
                globalHandler.getUserUnreadChats().put(recipientId, recipientChats);
                GlobalAnswer answer = new GlobalAnswer(GlobalSocketMessageTypes.UNREAD_CHATS.name(), 1);
                globalHandler.sendObject(recipientSession, answer);
            }
        }
        long duration = System.nanoTime() - start;
        System.out.println("Время addUnreadChat выполнения: " + duration + " нс");
    }
}
