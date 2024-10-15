package rt.marson.syeta.dto.google.places;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AutocompletePredictionDto {
    @JsonProperty("place_id")
    private String placeId;
    @JsonProperty("main_text")
    private String mainText;
    @JsonProperty("secondary_text")
    private String secondaryText;
    @JsonProperty("distance_meters")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer distanceMeters;
}
