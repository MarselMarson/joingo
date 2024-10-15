package rt.marson.syeta.config.websocket.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import rt.marson.syeta.config.websocket.handler.GlobalAnswer;
import rt.marson.syeta.config.websocket.handler.GlobalSocketMessageTypes;
import rt.marson.syeta.config.websocket.handler.GlobalWebSocketHandler;
import rt.marson.syeta.dto.event.EventNotificationDto;
import rt.marson.syeta.entity.Event;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.entity.notification.EventNotification;
import rt.marson.syeta.service.event.UserEventNotificationsService;
import rt.marson.syeta.util.DateUtil;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@DependsOn("globalWebSocketOperations")
public class EventWebSocketOperations {
    private final EventNotificationWebSocketHandler eventHandler;
    private final GlobalWebSocketHandler globalHandler;
    private final UserEventNotificationsService notificationsService;

    @Async("taskExecutor")
    public void sendNotification(EventNotification notification) {
        User target = notification.getTarget();
        Event event = notification.getEvent();
        System.out.println(eventHandler.getUserSessions());
        WebSocketSession globalSession = globalHandler.getUserSessions().get(target.getId());
        var userSessions = eventHandler.getUserSessions();
        WebSocketSession eventSession = userSessions.get(target.getId());
        if (globalSession != null) {
            Set<Long> userEvents = globalHandler.getUserNotifiedEvents().get(target.getId());

            boolean sendMessage = false;

            if (userEvents != null) {
                if (userEvents.add(event.getId())) {
                    sendMessage = true;
                }
            } else {
                userEvents = ConcurrentHashMap.newKeySet();
                userEvents.add(event.getId());
                globalHandler.getUserNotifiedEvents().put(target.getId(), userEvents);
                sendMessage = true;
            }
            GlobalAnswer answer = new GlobalAnswer(GlobalSocketMessageTypes.EVENT_NOTIFICATIONS.name(), userEvents.size());

            if (eventSession != null) {
                EventNotificationDto notificationDto = getNotificationDtoForNew(notification);
                try {
                    sendEventMessage(eventSession, notificationDto);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            if (sendMessage) {
                 try {
                    globalHandler.sendObject(globalSession, answer);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    @Async("taskExecutor")
    public void cancelNotification(EventNotification notification) {
        User target = notification.getTarget();
        Event event = notification.getEvent();
        WebSocketSession eventSession = eventHandler.getUserSessions().get(target.getId());
        WebSocketSession globalSession = globalHandler.getUserSessions().get(target.getId());

        if (globalSession != null) {
            Set<Long> userEvents = globalHandler.getUserNotifiedEvents().get(target.getId());
            if (userEvents != null) {
                System.out.println(userEvents);
                if (userEvents.remove(event.getId())) {
                    GlobalAnswer answer = new GlobalAnswer(GlobalSocketMessageTypes.EVENT_NOTIFICATIONS.name(),
                            userEvents.size());
                    try {
                        if (eventSession != null) {
                            EventNotificationDto notificationDto = getNotificationDtoForRemove(event, target);
                            sendEventMessage(eventSession, notificationDto);
                        }
                        globalHandler.sendObject(globalSession, answer);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Async("taskExecutor")
    public void sendEventMessage(WebSocketSession session, Object object) throws Exception {
        eventHandler.sendObject(session, object);
    }

    public EventNotificationDto getNotificationDtoForNew(EventNotification notification) {
        User target = notification.getTarget();
        Event event = notification.getEvent();
        String eventPhotoUrl = null;
        if (event.getPhoto() != null && !event.getPhoto().isEmpty()) {
            eventPhotoUrl = event.getPhoto().getFirst().getUrl();
        }

        List<EventNotification> notifications = notificationsService.getEventNotifications(target.getId(), event).stream()
                .distinct()
                .toList();

        Long notificationCount = (long) notifications.size();

        return EventNotificationDto.builder()
                .eventId(event.getId())
                .eventName(event.getName())
                .typeName(event.getType().getName())
                .firstPhotoUrl(eventPhotoUrl)
                .createDate(DateUtil.localToString(notification.getCreatedAt()))
                .notificationCount(notificationCount)
                .build();
    }

    public EventNotificationDto getNotificationDtoForRemove(Event event, User target) {
        String eventPhotoUrl = null;
        if (event.getPhoto() != null && !event.getPhoto().isEmpty()) {
            eventPhotoUrl = event.getPhoto().getFirst().getUrl();
        }

        List<EventNotification> notifications = notificationsService.getEventNotifications(target.getId(), event).stream()
                .distinct()
                .sorted(Comparator.comparing(EventNotification::getCreatedAt))
                .toList();

        LocalDateTime lastNotificationDate = LocalDateTime.now();
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
}
