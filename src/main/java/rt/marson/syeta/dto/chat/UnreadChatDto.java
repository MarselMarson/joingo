package rt.marson.syeta.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnreadChatDto {
    private final Long chatId;
    private final boolean isUnread;
}
