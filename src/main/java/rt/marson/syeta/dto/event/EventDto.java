package rt.marson.syeta.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import rt.marson.syeta.dto.LanguageDto;
import rt.marson.syeta.dto.ScheduleDto;
import rt.marson.syeta.dto.TagDto;
import rt.marson.syeta.dto.TypeDto;
import rt.marson.syeta.dto.user.UserForEventGetDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Ивент")
public class EventDto {
    @Schema(description = "id ивента")
    Long id;
    @Schema(description = "название")
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
    @Schema(description = "расстояние до пользователя")
    Double distanceToUser;

    @Schema(description = "дата начала")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime startDate;
    @Schema(description = "дата конца")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime endDate;
    @Schema(description = "дата создания ивента")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdDate;

    @Schema(description = "открытый ивент")
        @JsonProperty
    String isPublic;
    @Schema(description = "открытый ивент")
        @JsonProperty
    String isActive;
    @Schema(description = "автоподтверждение")
        @NotNull
    String confirmation;
    @Schema(description = "ограничение на кол-во участников")
    Integer limitParticipants;
    @Schema(description = "кол-во участников")
    Integer participantsCount;

    @Schema(description = "создатель ивента")
    UserForEventGetDto owner;
    @Schema(description = "участники ивента")
    List<UserForEventGetDto> participants;
    @Schema(description = "пользователи отправившие запрос на участие")
    List<UserForEventGetDto> confirmationParticipants;
    @Schema(description = "языки")
    List<LanguageDto> language;

    @Schema(description = "тип ивента")
    TypeDto type;
    @Schema(description = "тэги ивента")
    List<TagDto> tags;

    @Schema(description = "фотографии ивента")
    List<String> photo;

    @Schema(description = "в избранном")
    Boolean inFavourite;
    @Schema(description = "принимает участие в ивенте", example = "true/false/wait")
        @JsonProperty
    String isParticipate;
}
