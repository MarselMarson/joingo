package rt.marson.syeta.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rt.marson.syeta.dto.user.EmailExistDto;
import rt.marson.syeta.service.user.UserService;

@RestController
@RequestMapping("/checkEmail")
@RequiredArgsConstructor
@Tag(name = "Email")
@SecurityRequirements
public class EmailController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Существует ли уже такой email")
    public EmailExistDto isEmailAlreadyExists(@RequestParam String email) {
        return userService.getEmailExistDto(email);
    }
}
