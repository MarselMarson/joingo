package rt.marson.syeta.dto.event;

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
@Schema(description = "Уведомления об ивенте")
public class EventNotificationDto {
    @Schema(description = "id ивента")
    Long eventId;
    @Schema(description = "название ивента")
    String eventName;
    @Schema(description = "первое фото ивента")
    String firstPhotoUrl;
    @Schema(description = "название типа")
    String typeName;
    @Schema(description = "кол-во уведомлений")
    Long notificationCount;
    @Schema(description = "дата уведомления")
    String createDate;
}
