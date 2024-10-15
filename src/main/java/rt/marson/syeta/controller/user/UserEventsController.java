package rt.marson.syeta.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rt.marson.syeta.dto.event.EventDto;
import rt.marson.syeta.filter.EventFilter;
import rt.marson.syeta.service.user.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Операции с ивентами пользователем")
public class UserEventsController {
    private final UserService userService;

    @PreAuthorize("#userId == principal.id")
    @GetMapping("/{userId}/events")
    @Operation(summary = "Созданные ивенты")
    public Page<EventDto> getCreatedEvents(@PathVariable Long userId,
                                           EventFilter filter,
                                           @PageableDefault(sort = "startDate", size = 15) Pageable pageable) {
        filter.setActive(true);
        return userService.getUserEvents(userId, filter, pageable);
    }

    @PreAuthorize("#userId == principal.id")
    @GetMapping("/{userId}/events/archived")
    @Operation(summary = "Удаленные ивенты")
    public Page<EventDto> getArchivedEvents(@PathVariable Long userId,
                                            EventFilter filter,
                                            @PageableDefault(sort = "startDate", size = 15) Pageable pageable) {
        filter.setActive(false);
        return userService.getUserEvents(userId, filter, pageable);
    }

    @PreAuthorize("#userId == principal.id")
    @GetMapping("/{userId}/participateEvents")
    @Operation(summary = "Ивенты в которых пользователь принимает участие и дал запрос на участие")
    public Page<EventDto> getParticipateEvents(@PathVariable Long userId,
                                               EventFilter filter,
                                               @PageableDefault(sort = "startDate", size = 15) Pageable pageable) {
        filter.setActive(true);
        return userService.getParticipateEvents(userId, filter, pageable);
    }

    @PreAuthorize("#userId == principal.id")
    @GetMapping("/{userId}/favourites")
    @Operation(summary = "Избранные")
    public Page<EventDto> getFavouriteEvents(@PathVariable Long userId,
                                             EventFilter filter,
                                             @PageableDefault(sort = "startDate", size = 15) Pageable pageable) {
        filter.setActive(true);
        return userService.getAllFavouriteEvents(userId, filter, pageable);
    }
}
