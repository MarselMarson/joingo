package rt.marson.syeta.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rt.marson.syeta.config.websocket.event.EventWebSocketOperations;
import rt.marson.syeta.dto.event.EventNotificationDto;
import rt.marson.syeta.entity.Event;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.entity.notification.EventNotification;
import rt.marson.syeta.entity.notification.EventParticipateRequestNotification;
import rt.marson.syeta.repository.notification.EventNotificationRepo;
import rt.marson.syeta.repository.notification.EventParticipateRequestNotificationRepo;
import rt.marson.syeta.service.user.UserService;
import rt.marson.syeta.util.DateUtil;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class EventNotificationService {
    private final EventNotificationRepo notificationRepo;
    private final EventParticipateRequestNotificationRepo requestNotificationRepo;
    private final UserService userService;
    private final EventWebSocketOperations eventWebSocketOperations;

    @Async("taskExecutor")
    public CompletableFuture<EventNotification> createNotificationFuture(Event event, User target) {
        EventNotification notification = createNotification(event, target);
        return CompletableFuture.completedFuture(save(notification));
    }

    public List<EventNotification> getEventNotifications(Long userId) {
        return notificationRepo.findAllByTargetIdAndIsActiveTrue(userId);
    }
    public List<EventNotification> getEventNotifications(Long userId, Event event) {
        return notificationRepo.findAllByTargetIdAndEventAndIsActiveTrue(userId, event);
    }

    public CompletableFuture<EventParticipateRequestNotification> createParticipateRequestNotificationFuture(Event event, User participant) {
        EventNotification eventNotification = createNotification(event, event.getOwner());
        notificationRepo.save(eventNotification);
        EventParticipateRequestNotification notification = createParticipateRequestNotification(event, participant, eventNotification);
        requestNotificationRepo.save(notification);
        return CompletableFuture.completedFuture(saveParticipateRequestNotification(notification));
    }

    public EventNotification createNotification(Event event, User target) {
        return EventNotification.builder()
                .target(target)
                .event(event)
                .isActive(true)
                .build();
    }

    public void createAndSendNotification(Event event, User user) {
        CompletableFuture<EventNotification> futureNotification = createNotificationFuture(event, user);
        futureNotification.thenAccept(eventWebSocketOperations::sendNotification);
    }

    public void createAndSendParticipateRequestNotification(Event event, User participant) {
        var futureRequestNotification = createParticipateRequestNotificationFuture(event, participant);
        futureRequestNotification.thenAccept(requestNotification ->
                eventWebSocketOperations.sendNotification(requestNotification.getNotification()));
    }

    public EventParticipateRequestNotification createParticipateRequestNotification(Event event, User participant, EventNotification notification) {
        return EventParticipateRequestNotification.builder()
                .event(event)
                .participant(participant)
                .notification(notification)
                .isActive(true)
                .build();
    }

    public EventNotification save(EventNotification eventNotification) {
        return notificationRepo.saveAndFlush(eventNotification);
    }

    public EventParticipateRequestNotification saveParticipateRequestNotification(EventParticipateRequestNotification notification) {
        return requestNotificationRepo.saveAndFlush(notification);
    }

    @Async
    @Transactional
    public void cancelParticipateRequest(Event event, User participant) {
        EventParticipateRequestNotification participateRequestNotification = requestNotificationRepo
                .findByParticipantAndEventAndIsActiveTrue(participant, event);
        participateRequestNotification.setIsActive(false);
        EventNotification notification = participateRequestNotification.getNotification();
        notification.setIsActive(false);
        requestNotificationRepo.saveAndFlush(participateRequestNotification);
        notificationRepo.saveAndFlush(notification);
        eventWebSocketOperations.cancelNotification(notification);
    }

    public List<EventNotificationDto> getAllNotifications(Long userId) {
        User user = userService.findUserById(userId);
        return getEventNotifications(userId).stream()
                .map(EventNotification::getEvent)
                .distinct()
                .map(event -> toDto(event, user))
                .sorted(Comparator.comparing(EventNotificationDto::getCreateDate).reversed())
                .toList();
    }

    public EventNotificationDto toDto(Event event, User target) {
        String eventPhotoUrl = null;
        if (event.getPhoto() != null && !event.getPhoto().isEmpty()) {
            eventPhotoUrl = event.getPhoto().getFirst().getUrl();
        }

        List<EventNotification> notifications = getEventNotifications(target.getId(), event).stream()
                .filter(EventNotification::getIsActive)
                .distinct()
                .sorted(Comparator.comparing(EventNotification::getCreatedAt))
                .toList();

        LocalDateTime lastNotificationDate = null;
        if (!notifications.isEmpty()) {
            lastNotificationDate = notifications.getLast().getCreatedAt();
        }

        Long notificationCount = (long) notifications.size();

        return EventNotificationDto.builder()
                .eventId(event.getId())
                .eventName(event.getName())
                .typeName(event.getType().getName())
                .firstPhotoUrl(eventPhotoUrl)
                .createDate(DateUtil.localToString(lastNotificationDate))
                .notificationCount(notificationCount)
                .build();
    }

    @Transactional
    public void deleteAllNotifications(Long userId) {
        getEventNotifications(userId)
                .forEach(notification -> notification.setIsActive(false));
    }
}
