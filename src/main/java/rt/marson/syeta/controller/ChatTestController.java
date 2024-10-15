package rt.marson.syeta.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "Чат")
public class ChatTestController {
    @GetMapping
    public ResponseEntity<String> getMsgs() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        [
                           {
                             "date": "2024-09-01T08:00:00+03:00",
                             "owner": "1",
                             "message": "Привет"
                           },
                           {
                             "date": "2024-09-01T08:01:00+03:00",
                             "owner": "1",
                             "message": "Как дела"
                           },
                           {
                             "date": "2024-09-01T08:45:00+03:00",
                             "owner": "2",
                             "message": "Здарова, отлично!"
                           },
                           {
                             "date": "2024-09-01T09:27:00+03:00",
                             "owner": "1",
                             "message": "Ты в Кукморе?"
                           },
                           {
                             "date": "2024-09-01T10:12:00+03:00",
                             "owner": "2",
                             "message": "Неа"
                           },
                           {
                             "date": "2024-09-01T10:12:00+03:00",
                             "owner": "2",
                             "message": "в Маямчике тусуюсь"
                           },
                           {
                             "date": "2024-09-01T14:46:00+03:00",
                             "owner": "1",
                             "message": "Ебать ты красавчик! Надолго там?"
                           },
                           {
                             "date": "2024-09-01T14:46:30+03:00",
                             "owner": "2",
                             "message": "Неделю еще отдохну"
                           },
                           {
                             "date": "2024-10-01T06:03:00+03:00",
                             "owner": "1",
                             "message": "Заебись, сколько телки там стоят?"
                           },
                           {
                             "date": "2024-10-01T12:22:00+03:00",
                             "owner": "2",
                             "message": "хз, тиндер рулит! все бесплатно"
                           },
                           {
                             "date": "2024-10-01T12:22:00+03:00",
                             "owner": "2",
                             "message": "Как у тебя дел? как погода? Пишут что у вас погода полное говно… типа град, ливень и хз что еще..."
                           },
                           {
                             "date": "2024-10-01T22:26:00+03:00",
                             "owner": "1",
                             "message": "Все заебись, по погоде врут! у нас просто апокалипсис"
                           },
                           {
                             "date": "2024-11-01T02:02:00+03:00",
                             "owner": "1",
                             "message": "Ебанько?"
                           }
                         ]
                """);
    }
}
