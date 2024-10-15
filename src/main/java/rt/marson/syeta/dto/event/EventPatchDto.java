package rt.marson.syeta.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import rt.marson.syeta.dto.LanguageDto;
import rt.marson.syeta.dto.ScheduleDto;
import rt.marson.syeta.dto.TagDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Обновление ивента")
public class EventPatchDto {
    @Schema(description = "название")
        @NotNull
    String name;
    @Schema(description = "описание")
    String description;
    @Schema(description = "информация")
    String info;
    @Schema(description = "расписание")
    List<ScheduleDto> schedule;

    @Schema(description = "локация")
    String locationLatLng;
    @Schema(description = "город")
    String locationCity;
    @Schema(description = "шташ")
    String locationState;
    @Schema(description = "страна")
    String locationCountry;
    @Schema(description = "полный адрес")
    String locationFullAddress;

    @Schema(description = "дата начала")
        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime startDate;
    @Schema(description = "дата конца")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime endDate;

    @Schema(description = "открытый ивент")
        @NotNull
        @JsonProperty
    String isPublic;
    @Schema(description = "автоподтверждение")
        @NotNull
    String confirmation;
    @Schema(description = "кол-во участников")
    Integer limitParticipants;
    @Schema(description = "языки")
    List<LanguageDto> language;

    @Schema(description = "id типа ивента")
        @NotNull
    Short type;
    @Schema(description = "тэги ивента")
    List<TagDto> tags;

    @Schema(description = "фотографии ивента")
    List<String> photo;
}

