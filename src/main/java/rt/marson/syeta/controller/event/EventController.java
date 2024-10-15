package rt.marson.syeta.controller.event;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import rt.marson.syeta.dto.auth.AuthUserId;
import rt.marson.syeta.dto.event.EventCreateDto;
import rt.marson.syeta.dto.event.EventDto;
import rt.marson.syeta.dto.event.EventPatchDto;
import rt.marson.syeta.filter.EventFilter;
import rt.marson.syeta.service.event.EventService;

@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
@Tag(name = "Операции с эвентом")
public class EventController {
    private final EventService eventService;

    @PostMapping
    @Operation(summary = "Создать ивент")
    public EventDto createEvent(@AuthUserId Long authUserId,
                                @Valid @RequestBody EventCreateDto eventDto) {
        return eventService.createEvent(eventDto, authUserId);
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Получить ивент по id")
    public EventDto getEventById(@PathVariable Long eventId, @RequestParam(name = "userLocation", required = false) String userLocation) {
        return eventService.getEventById(eventId, userLocation);
    }

    @GetMapping
    @Operation(summary = "Получить все ивенты")
    public Page<EventDto> getAllEvents(
                                EventFilter filter,
                                @PageableDefault(sort = "startDate", size = 15) Pageable pageable) {
        filter.setActive(true);
        filter.setIsPublic(true);
        return eventService.getAllEvents(filter, pageable);
    }

    @GetMapping("/archived")
    @Operation(summary = "Получить все неактивные ивенты")
    public Page<EventDto> getAllNonActiveEvents(
                                EventFilter filter,
                                @PageableDefault(sort = "startDate", size = 15) Pageable pageable) {
        filter.setActive(false);
        return eventService.getAllEvents(filter, pageable);
    }

    @GetMapping("/archived/{eventId}")
    @Operation(summary = "Получить неактивный ивент по id")
    public EventDto getNonActiveEventById(@PathVariable Long eventId) {
        return eventService.getNonActiveEventById(eventId);
    }

    @PatchMapping("/{eventId}")
    @Operation(summary = "Изменить ивент")
    public EventDto updateEvent(@AuthUserId Long authUserId,
                                @PathVariable Long eventId,
                                @Valid @RequestBody EventPatchDto eventDto) {
        return eventService.updateEvent(authUserId, eventId, eventDto);
    }

    @DeleteMapping("/{eventId}")
    @Operation(summary = "Удалить ивент")
    public void deleteEvent(@AuthUserId Long authUserId, @PathVariable Long eventId) {
        eventService.deleteEvent(authUserId, eventId);
    }
}
