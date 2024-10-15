package rt.marson.syeta.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import rt.marson.syeta.dto.CountryDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Пользователь")
public class UserDto {
    @Schema(description = "id пользователя")
    Long id;
    @Schema(description = "Имя пользователя")
    String firstName;
    @Schema(description = "Фамилия пользователя")
    String lastName;
    @Schema(description = "email", example = "a@a.a")
    String email;
    @Schema(description = "Описание или комментарий")
    String description;
    @Schema(description = "Фотографии пользователя")
    String photoUrl;

    @Schema(description = "Дата создания аккаунта")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt;
    @Schema(description = "Дата рождения")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime birthDate;

    @Schema(description = "Страна пользователя")
    CountryDto country;
    @Schema(description = "Подтвержден ли email")
    boolean isEmailVerified;
}
