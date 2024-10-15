package rt.marson.syeta.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Url фото")
public class FileUrlDto {
    @Schema(description = "Url файла", example = "dropbox.com/photo324.png")
    String url;
}
