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
@RequestMapping("/confirm")
@RequiredArgsConstructor
@Tag(name = "Подтверждение почты")
public class ConfirmEmailController {
    private final MailService mailService;
    /*@GetMapping()
    public String confirmEmail(@RequestParam String token, Model model) {
        if (mailService.confirmEmail(token)) {
            model.addAttribute("answer", "Почта успешно подтверждена");
        } else {
            model.addAttribute("answer", "Время на подтверждение почты истекло");
        }
        return "confirmEmail";
    }*/

    @GetMapping
    public String showConfirmEmailPage(@RequestParam(name = "token") String token, Model model) {
        model.addAttribute("token", token);
        return "confirmEmailFetch"; // это шаблон FreeMarker
    }

    // Обработка асинхронного запроса для подтверждения email
    @GetMapping("/process")
    public ResponseEntity<String> confirmEmail(@RequestParam(name = "token") String token) {
        String message = mailService.confirmEmail(token)
                ? "Почта успешно подтверждена"
                : "Время на подтверждение почты истекло";

        return ResponseEntity.ok(message);
    }
}
