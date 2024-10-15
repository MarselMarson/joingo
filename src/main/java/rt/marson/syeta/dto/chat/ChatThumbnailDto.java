package rt.marson.syeta.dto.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Миниатюра чата для списка чатов")
public class ChatThumbnailDto {
    @Schema(description = "id собеседника")
    Long userId;
    @Schema(description = "url фото собеседника")
    String photoUrl;
    @Schema(description = "имя собеседника")
    String firstName;
    @Schema(description = "фамилия собеседника")
    String lastName;
    @Schema(description = "кол-во непрочитанных сообщений")
    Integer numberUnread;
    @Schema(description = "id последнего сообщения в чате")
    Long lastMessageId;
    @Schema(description = "текст последнего сообщения в чате")
    String lastMessageContent;
    @Schema(description = "дата последнего сообщения в чате")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    String lastMessageDate;
}
