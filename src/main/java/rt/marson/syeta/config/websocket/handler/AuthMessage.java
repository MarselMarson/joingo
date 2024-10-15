package rt.marson.syeta.config.websocket.handler;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthMessage {
    String jwt;
    Long chatPartnerId;
}
