package rt.marson.syeta.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rt.marson.syeta.entity.Event;
import rt.marson.syeta.entity.notification.EventNotification;
import rt.marson.syeta.repository.notification.EventNotificationRepo;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserEventNotificationsService {
    private final EventNotificationRepo notificationRepo;

    public Set<Long> getNotificationEventsId(Long userId) {
        return getEventsNotifications(userId).stream()
                .map(notification -> notification.getEvent().getId())
                .collect(Collectors.toSet());
    }

    public List<EventNotification> getEventsNotifications(Long userId) {
        return notificationRepo.findAllByTargetIdAndIsActiveTrue(userId);
    }

    public List<EventNotification> getEventNotifications(Long userId, Event event) {
        return notificationRepo.findAllByTargetIdAndEventAndIsActiveTrue(userId, event);
    }
}
