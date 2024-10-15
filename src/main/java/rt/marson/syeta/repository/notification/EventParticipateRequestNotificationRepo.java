package rt.marson.syeta.repository.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rt.marson.syeta.entity.Event;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.entity.notification.EventParticipateRequestNotification;

@Repository
public interface EventParticipateRequestNotificationRepo extends JpaRepository<EventParticipateRequestNotification, Long> {
    EventParticipateRequestNotification findByParticipantAndEventAndIsActiveTrue(User participant, Event event);
}
