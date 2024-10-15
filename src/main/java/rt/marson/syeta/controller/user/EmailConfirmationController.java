package rt.marson.syeta.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rt.marson.syeta.dto.auth.AuthUserId;
import rt.marson.syeta.service.MailService;

@RestController
@RequestMapping("/confirmEmail")
@RequiredArgsConstructor
@Tag(name = "Email confirmation")
@SecurityRequirements
public class EmailConfirmationController {
    private final MailService mailService;

    @PostMapping
    @Operation(summary = "Отправить подтверждение")
    public void sendConfirmation(@AuthUserId Long authUserId) {
        mailService.sendEmailConfirmation(authUserId);
    }
}
