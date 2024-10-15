package rt.marson.syeta.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/about-us")
@Tag(name = "О нас")
public class AboutUsController {
    @GetMapping("/privacy-policy")
    public String getConfidential(Model model) {
        return "confidential";
    }
    @GetMapping("/terms-of-use")
    public String getTermsOfUse(Model model) {
        return "termsOfUse";
    }
}
