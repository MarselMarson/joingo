package rt.marson.syeta.dto.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Тет-а-тет чат")
public class ChatDto {
    @Schema(description = "id собеседника")
    Long userId;
    @Schema(description = "url фото собеседника")
    String photoUrl;
    @Schema(description = "имя собеседника")
    String firstName;
    @Schema(description = "фамилия собеседника")
    String lastName;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @Schema(description = "сообщения")
    List<ChatMessageDto> messages;
}
