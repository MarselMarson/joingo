package rt.marson.syeta.filter;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFilter {
    String name;
    LocalDateTime dateStartMoreThan;
    LocalDateTime dateStartLessThan;
    String userLocation;
    boolean isActive;
    Boolean isPublic;
}
