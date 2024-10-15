package rt.marson.syeta.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rt.marson.syeta.entity.Event;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.dto.user.UserForEventGetDto;
import rt.marson.syeta.mapper.UserMapper;
import rt.marson.syeta.repository.EventRepo;
import rt.marson.syeta.service.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class EventParticipantsService {
    private final EventService eventService;
    private final EventRepo eventRepo;
    @Lazy
    private final UserService userService;
    private final UserMapper userMapper;
    private final EventNotificationService notificationService;

    public List<UserForEventGetDto> getParticipants(Long eventId, Long authUserId) {
        Event event = eventService.findActiveEventById(eventId);
        userService.compareUsersIds(event.getOwner().getId(), authUserId);

        return userMapper.toEventUserDots(event.getParticipants()
                .stream().toList());
    }

    public List<UserForEventGetDto> getConfirmParticipants(Long eventId, Long authUserId) {
        Event event = eventService.findActiveEventById(eventId);
        userService.compareUsersIds(event.getOwner().getId(), authUserId);

        return userMapper.toEventUserDots(event.getConfirmationParticipants()
                .stream()
                .toList()
        );
    }

    @Transactional
    public void participate(Long eventId, Long authUserId) {
        User user = userService.findUserById(authUserId);
        Event event = eventService.findActiveEventById(eventId);

        if (event.getParticipants().contains(user)) {
            throw new IllegalArgumentException("User id: " +
                    authUserId + " is the participant already");
        }

        if (event.getConfirmationParticipants().contains(user)) {
            throw new IllegalArgumentException("User id: " +
                    authUserId + " sent request already");
        }

        int participantsCount = event.getParticipants().size();
        if (event.getLimitParticipants() != null && event.getLimitParticipants() <= participantsCount) {
            throw new AccessDeniedException("The number of participants is filled");
        }

        if (!event.isConfirmation() || event.getOwner().equals(user)) {
            event.getParticipants().add(user);
        } else {
            event.getConfirmationParticipants().add(user);
            notificationService.createAndSendParticipateRequestNotification(event, user);
        }

        eventRepo.saveAndFlush(event);
    }

    @Transactional
    public void leaveEvent(Long eventId, Long authUserId) {
        User user = userService.findUserById(authUserId);
        Event event = eventService.findActiveEventById(eventId);
        if (!event.getParticipants().contains(user)) {
            throw new AccessDeniedException("User id: " +
                    authUserId + " is not the participant yet");
        }

        event.getParticipants().remove(user);
        eventRepo.saveAndFlush(event);
    }

    @Transactional
    public void removeParticipantById(Long eventId, Long removeUserId, Long authUserId) {
        Event event = eventService.findActiveEventById(eventId);
        checkIsAuthUserEventOwner(event, authUserId);

        User removedUser = userService.findUserById(removeUserId);
        if (!event.getParticipants().remove(removedUser)) {
            throw new IllegalArgumentException("User id: " +
                    removeUserId + " is not the participant of the event id: " + eventId);
        }
    }

    public void checkIsAuthUserEventOwner(Event event, Long authUserId) {
        userService.compareUsersIds(event.getOwner().getId(), authUserId);
    }

    @Transactional
    public void acceptParticipant(Long eventId, Long userId, Long authUserId) {
        Event event = eventService.findActiveEventById(eventId);
        checkIsAuthUserEventOwner(event, authUserId);

        User participant = userService.findUserById(userId);
        if (!event.getConfirmationParticipants().remove(participant)) {
            throw new IllegalArgumentException("User id: " +
                    userId + " was not found in the list of confirmation requests event id: " +
                    event.getId());
        }

        event.getParticipants().add(participant);
        notificationService.createAndSendNotification(event, participant);
        eventRepo.saveAndFlush(event);
    }

    @Transactional
    public void declineParticipant(Long eventId, Long userId, Long authUserId) {
        Event event = eventService.findActiveEventById(eventId);
        checkIsAuthUserEventOwner(event, authUserId);

        User participant = userService.findUserById(userId);
        if (!event.getConfirmationParticipants().remove(participant)) {
            throw new IllegalArgumentException("User id: " +
                    userId + " was not found in the list of confirmation requests event id: " +
                    event.getId());
        }

        notificationService.createAndSendNotification(event, participant);

        eventRepo.saveAndFlush(event);
    }

    @Transactional
    public void cancelParticipationRequest(Long eventId, Long authUserId) {
        Event event = eventService.findActiveEventById(eventId);
        User user = userService.findUserById(authUserId);
        if (!event.getConfirmationParticipants().remove(user)) {
            throw new AccessDeniedException("User id: " +
                    authUserId + " was not found in the list of confirmation requests event id: " +
                    event.getId());
        }
        notificationService.cancelParticipateRequest(event, user);
        eventRepo.saveAndFlush(event);
    }
}
