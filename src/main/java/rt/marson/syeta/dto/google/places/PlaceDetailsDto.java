package rt.marson.syeta.dto.google.places;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceDetailsDto {
    @JsonProperty("formatted_address")
    private String formattedAddress;
    private String name;
    private String location;
    private String vicinity;
}
