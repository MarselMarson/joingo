package rt.marson.syeta.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Проверка на существование почты")
public class EmailExistDto {
    @Schema(description = "Ответ на существование email", example = "true/false")
    boolean isEmailExist;
}
