package rt.marson.syeta.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import rt.marson.syeta.dto.auth.AuthUserId;
import rt.marson.syeta.service.event.FavouriteEventService;

@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
@Tag(name = "Избранные ивенты")
public class FavouriteEventController {
    private final FavouriteEventService favouriteService;

    @PostMapping("/{eventId}/favourite")
    @Operation(summary = "Добавить ивент в избранное")
    public void add(@PathVariable Long eventId,
                    @AuthUserId Long authUserId) {
        favouriteService.addEventToFavourites(eventId, authUserId);
    }

    @DeleteMapping("/{eventId}/favourite")
    @Operation(summary = "Убрать ивент из избранного")
    public void remove(@PathVariable Long eventId,
                    @AuthUserId Long authUserId) {
        favouriteService.removeEventFromFavourites(eventId, authUserId);
    }
}
