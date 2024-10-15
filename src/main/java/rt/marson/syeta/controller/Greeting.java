package rt.marson.syeta.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import rt.marson.syeta.service.DelayService;

@RestController
@RequiredArgsConstructor
@SecurityRequirements
public class Greeting {
    private final DelayService delayService;

    @GetMapping("/hi")
    String greeting() {
        return "hello, bitches!";
    }

    @GetMapping("/start_delay")
    boolean startDelay() {
        return delayService.startDelay();
    }

    @GetMapping("/stop_delay")
    boolean stopDelay() {
        return delayService.stopDelay();
    }
}
