package rt.marson.syeta.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventWithDistanceDto {
    private Long eventId;
    private Double distance;
}
