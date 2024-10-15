package rt.marson.syeta.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import rt.marson.syeta.dto.auth.JwtAuthenticationResponseDto;
import rt.marson.syeta.dto.auth.SignInRequestDto;
import rt.marson.syeta.dto.auth.SignUpRequestDto;
import rt.marson.syeta.security.AuthenticationService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
@SecurityRequirements
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public JwtAuthenticationResponseDto signUp(@RequestBody @Valid SignUpRequestDto request) {
        return authenticationService.signUp(request);
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/sign-in")
    public JwtAuthenticationResponseDto signIn(@RequestBody @Valid SignInRequestDto request) {
        return authenticationService.signIn(request);
    }

    @Operation(summary = "восстановить пароль")
    @PostMapping("/reset-password")
    public void resetPassword(@RequestParam(name = "email") String email) {
        authenticationService.resetPassword(email);
    }
}