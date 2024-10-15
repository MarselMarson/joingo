package rt.marson.syeta.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@Schema(description = "Расписание ивента")
public class ScheduleDto {
    @Schema(description = "время начала")
    String startDate;
    @Schema(description = "описание")
    String description;
}
