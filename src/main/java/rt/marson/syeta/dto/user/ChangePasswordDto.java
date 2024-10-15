package rt.marson.syeta.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на смену пароля")
public class ChangePasswordDto {
    @Schema(description = "Старый пароль", example = "old_pass")
    @NotBlank
    private String oldPassword;

    @Schema(description = "Новый пароль", example = "new_pass")
    @Size(min = 8, max = 24, message = "Длина пароля должна быть от 8 до 24 символов")
    @NotBlank
    private String newPassword;
}
