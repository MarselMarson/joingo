package rt.marson.syeta.repository.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rt.marson.syeta.entity.Event;
import rt.marson.syeta.entity.notification.EventNotification;

import java.util.List;

@Repository
public interface EventNotificationRepo extends JpaRepository<EventNotification, Long> {
    List<EventNotification> findAllByTargetIdAndEventAndIsActiveTrue(Long targetId, Event event);
    List<EventNotification> findAllByTargetIdAndIsActiveTrue(Long targetId);
}
