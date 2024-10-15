package rt.marson.syeta.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Запрос на регистрацию")
public class SignUpRequestDto {
    @Schema(description = "Адрес электронной почты", example = "johndoe@gmail.com")
    @Size(min = 5, max = 50, message = "Адрес электронной почты должен содержать от 5 до 50 символов")
    @NotBlank(message = "Адрес электронной почты не может быть пустыми")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String email;

    @Schema(description = "Пароль", example = "my_1secret1_Password")
    @Size(min = 8, max = 24, message = "Длина пароля должна быть от 8 до 24 символов")
    @NotBlank
    private String password;

    @Schema(description = "Имя пользователя", example = "John")
    @NotBlank(message = "Имя не может быть пустыми")
    private String firstName;

    @Schema(description = "Фамилия пользователя", example = "Doe")
    @NotBlank(message = "Фамилия не может быть пустыми")
    private String lastName;

    @Schema(description = "Страна", example = "France")
    private String countryName;

    @Schema(description = "дата рождения")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime birthDate;
}
