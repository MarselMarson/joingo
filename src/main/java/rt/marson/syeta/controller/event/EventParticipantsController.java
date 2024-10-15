package rt.marson.syeta.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import rt.marson.syeta.dto.auth.AuthUserId;
import rt.marson.syeta.dto.user.UserForEventGetDto;
import rt.marson.syeta.service.event.EventParticipantsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/event/{eventId}")
@RequiredArgsConstructor
@Tag(name = "Модерация пользователей ивента")
public class EventParticipantsController {
    private final EventParticipantsService eventParticipantsService;

    @PostMapping("/participate")
    @Operation(summary = "Участвовать в ивенте")
    public void participate(@PathVariable Long eventId,
                            @AuthUserId Long authUserId) {
        eventParticipantsService.participate(eventId, authUserId);
    }

    @DeleteMapping("/participate")
    @Operation(summary = "Покинуть ивент")
    public void dontParticipate(@PathVariable Long eventId,
                                @AuthUserId Long authUserId) {
        eventParticipantsService.leaveEvent(eventId, authUserId);
    }

    @GetMapping("/participants")
    @Operation(summary = "Получить участников ивента")
    public List<UserForEventGetDto> getParticipants(@PathVariable Long eventId,
                                                    @AuthUserId Long authUserId) {
        return eventParticipantsService.getParticipants(eventId, authUserId);
    }

    @DeleteMapping("/participants/{removeUserId}")
    @Operation(summary = "Исключить участника")
    public void removeParticipant(@PathVariable Long eventId,
                                  @PathVariable Long removeUserId,
                                  @AuthUserId Long authUserId) {
        eventParticipantsService.removeParticipantById(eventId, removeUserId, authUserId);
    }

    @GetMapping("/participationRequests")
    @Operation(summary = "Список запросов на добавление в мероприятие")
    public List<UserForEventGetDto> getParticipationRequests(@PathVariable Long eventId,
                                                           @AuthUserId Long authUserId) {
        return eventParticipantsService.getConfirmParticipants(eventId, authUserId);
    }

    @PostMapping("/participationRequests/{userId}/accept")
    @Operation(summary = "Принять заявку пользователя")
    public void acceptParticipant(@PathVariable Long eventId,
                                  @PathVariable Long userId,
                                  @AuthUserId Long authUserId) {
        eventParticipantsService.acceptParticipant(eventId, userId, authUserId);
    }

    @PostMapping("/participationRequests/cancel")
    @Operation(summary = "Отменить запрос на участие")
    public void cancelParticipationRequest(@PathVariable Long eventId, @AuthUserId Long authUserId) {
        eventParticipantsService.cancelParticipationRequest(eventId, authUserId);
    }

    @PostMapping("/participationRequests/{userId}/decline")
    @Operation(summary = "Отклонить заявку пользователя")
    public void declineParticipant(@PathVariable Long eventId,
                                  @PathVariable Long userId,
                                  @AuthUserId Long authUserId) {
        eventParticipantsService.declineParticipant(eventId, userId, authUserId);
    }
}
