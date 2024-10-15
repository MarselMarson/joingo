package rt.marson.syeta.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/offer")
public class OfferController {

    private static final String APP_STORE_URL = "https://apps.apple.com/kz/app/my-talking-angela-2/id1536584509";
    private static final String PLAY_STORE_URL = "market://details?id=info.goodline.events";
    private static final String APP_LINK = "sueta://syeta.onrender.com/offer/";

    @GetMapping("/{offerId}")
    public String getOffer(Model model,
                           HttpServletRequest request,
                           @PathVariable Long offerId
    ) {
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT).toLowerCase();

        if (userAgent.contains("android")) {
            model.addAttribute("redirectUrl", APP_LINK + offerId);
            model.addAttribute("storeUrl", PLAY_STORE_URL);
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
            model.addAttribute("redirectUrl", APP_LINK + offerId);
            model.addAttribute("storeUrl", APP_STORE_URL);
        } else {
            model.addAttribute("redirectUrl", "https://syeta.onrender.com/hi");
            model.addAttribute("storeUrl", "https://syeta.onrender.com/hi");
        }

        return "openInApp";
    }
}
