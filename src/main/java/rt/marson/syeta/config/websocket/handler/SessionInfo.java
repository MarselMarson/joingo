package rt.marson.syeta.config.websocket.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SessionInfo {
    private Long userId;
    private Long chatId;
    private Long recipientId;

    public SessionInfo(Long userId) {
        this.userId = userId;
    }
}
