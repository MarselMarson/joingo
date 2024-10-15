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
@Schema(description = "Страна")
public class CountryDto {
    @Schema(description = "id страны")
    Long id;
    @Schema(description = "Название страны")
    String name;
    @Schema(description = "Название файла флага страны", example = "France.png")
    String flag;
}
