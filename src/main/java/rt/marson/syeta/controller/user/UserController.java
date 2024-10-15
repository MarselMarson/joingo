package rt.marson.syeta.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rt.marson.syeta.dto.user.ChangePasswordDto;
import rt.marson.syeta.dto.CountryDto;
import rt.marson.syeta.dto.user.UserDto;
import rt.marson.syeta.dto.user.UserPatchDto;
import rt.marson.syeta.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Операции с пользователем")
public class UserController {
    private final UserService userService;
    @GetMapping("/{userId}")
    @Operation(summary = "Получить пользователя по id")
    public UserDto getUserById(@PathVariable Long userId) {
        return userService.getActiveUserById(userId);
    }

    @GetMapping
    @Operation(summary = "Получить список всех пользователей")
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @PreAuthorize("#userId == principal.id")
    @PatchMapping("{userId}")
    @Operation(summary = "Обновить данные пользователя")
    public UserDto updateUser(@Valid @RequestBody UserPatchDto userDto,
                              @PathVariable Long userId) {
        return userService.updateUser(userDto, userId);
    }

    @PreAuthorize("#userId == principal.id")
    @DeleteMapping("/{userId}")
    @Operation(summary = "Удалить пользователя")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @PreAuthorize("#userId == principal.id")
    @PatchMapping("/{userId}/country")
    @Operation(summary = "Изменить страну пользователя по id")
    public void changeCountry(@PathVariable Long userId,
                              @RequestBody CountryDto countryDto) {
        userService.changeCountry(userId, countryDto);
    }

    @PreAuthorize("#userId == principal.id")
    @PatchMapping("/{userId}/changePassword")
    @Operation(summary = "Сменить пароль")
    public void changePassword(@PathVariable Long userId,
                               @RequestBody ChangePasswordDto changePasswordDto) {
        userService.changePassword(userId, changePasswordDto);
    }
}
