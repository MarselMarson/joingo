package rt.marson.syeta.controller.google.places;

import com.google.maps.model.LatLng;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SessionToken {
    private UUID sessionToken;
    private LocalDateTime creationTime;
    private String input;
    private LatLng location;
    private String language;

    public boolean isExpired() {
        // Проверка истечения времени сессии (1 минута 50 секунд)
        return LocalDateTime.now().isAfter(creationTime.plusSeconds(110));
    }
}
