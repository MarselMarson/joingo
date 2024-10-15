package rt.marson.syeta.controller.event;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rt.marson.syeta.dto.auth.AuthUserId;
import rt.marson.syeta.dto.event.EventNotificationDto;
import rt.marson.syeta.service.event.EventNotificationService;

import java.util.List;

@RestController
@RequestMapping("/event/notifications")
@RequiredArgsConstructor
@Tag(name = "Уведомления ивентов")
public class EventNotificationController {
    private final EventNotificationService notificationService;
    @GetMapping
    public List<EventNotificationDto> getNotifications(@AuthUserId Long userId) {
        return notificationService.getAllNotifications(userId);
    }

    @DeleteMapping
    public void deleteNotifications(@AuthUserId Long userId) {
        notificationService.deleteAllNotifications(userId);
    }
}
