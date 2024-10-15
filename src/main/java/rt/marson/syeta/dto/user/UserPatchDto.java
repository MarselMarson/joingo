package rt.marson.syeta.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import rt.marson.syeta.dto.validation.NullOrNotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Запрос на изменение пользователя")
public class UserPatchDto {
    @Schema(description = "Имя пользователя")
    @NullOrNotBlank
    String firstName;
    @Schema(description = "Фамилия пользователя")
    @NullOrNotBlank
    String lastName;
    @Schema(description = "Описание/комментарий")
    String description;
    @Schema(description = "Название файла фото пользователя", example = "saved.png")
    String photoUrl;
    @Schema(description = "дата рождения")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    String birthDate;
}
