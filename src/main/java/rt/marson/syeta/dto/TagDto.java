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
@Schema(description = "Тип ивента")
public class TagDto {
    @Schema(description = "id типа")
    Integer id;
    @Schema(description = "имя типа")
    String name;
}
