package rt.marson.syeta.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rt.marson.syeta.service.MailService;

@Controller
@RequestMapping("/confirm-password-reset")
@RequiredArgsConstructor
@Tag(name = "Подтверждение почты")
public class ResetPasswordController {
    private final MailService mailService;
    /*@GetMapping
    public String resetPassword(@RequestParam(name = "token") String token, Model model) {
        if (mailService.resetPassword(token)) {
            model.addAttribute("answer", "Новый пароль отправлен на Вашу почту");
        } else {
            model.addAttribute("answer", "Время на сброс пароля (24 часа) истекло");
        }
        return "resetPassword";
    }*/

    @GetMapping
    public String showResetPasswordPage(@RequestParam(name = "token") String token, Model model) {
        model.addAttribute("token", token);
        return "resetPasswordFetch"; // это шаблон FreeMarker
    }

    @GetMapping("/process")
    public ResponseEntity<String> resetPassword(@RequestParam(name = "token") String token) {
        String message = mailService.resetPassword(token)
                ? "Готово! Новый пароль отправлен на Вашу почту"
                : "Ой! Время на сброс пароля (24 часа) истекло";

        return ResponseEntity.ok(message);
    }
}
