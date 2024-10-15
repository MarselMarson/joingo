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
@Schema(description = "Сообщение из тет-а-тет чата")
public class ChatMessageDto {
    @Schema(description = "id сообщения")
    Long id;
    @Schema(description = "id сообщения frontend")
    Long sentId;
    @Schema(description = "id собеседника")
    Long chatPartnerId;
    @Schema(description = "id отправителя")
    Long senderId;

    @Schema(description = "Дата создания")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    String createdDate;

    @Schema(description = "сообщение")
    String content;
    @Schema(description = "статус сообщения")
    String status;
}
