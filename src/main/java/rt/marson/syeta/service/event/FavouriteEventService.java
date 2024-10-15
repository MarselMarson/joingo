package rt.marson.syeta.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rt.marson.syeta.entity.Event;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.service.user.UserService;

@Service
@RequiredArgsConstructor
public class FavouriteEventService {
    private final UserService userService;
    private final EventService eventService;

    public void addEventToFavourites(Long eventId, Long userId) {
        userService.addEventToFavourites(userId, eventId);
    }
    public void removeEventFromFavourites(Long eventId, Long userId) {
        User user = userService.findUserById(userId);
        Event event = eventService.findActiveEventById(eventId);
        if (user.getFavouriteEvents().contains(event)) {
            userService.removeEventFromFavourites(userId, eventId);
        }
    }
}
