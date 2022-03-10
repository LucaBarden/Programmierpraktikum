package de.propra.chicken.web;

import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Urlaub;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Controller
public class WebController {

    //TODO Security richtig einstellen

    @Secured("ROLE_USER")
    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("user",
                principal != null ? principal.getAttribute("login") : null
        );
        return "Student";
    }

    @Secured("ROLE_USER")
    @GetMapping("/student")
    public String student(Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("user",
                principal != null ? principal.getAttribute("login") : null
        );
        return "Student";
    }

    @Secured("ROLE_USER")
    @GetMapping("/urlaub")
    public String urlaub(@ModelAttribute Urlaub urlaub, Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("user",
                principal != null ? principal.getAttribute("login") : null);
        return "Urlaub";
    }

    @Secured("ROLE_USER")
    @PostMapping("/urlaubErstellen")
    public String urlaubErstellen(@ModelAttribute Urlaub urlaub, Model model) {
        // TODO Anlegen des Urlaubs

        System.out.println(urlaub.getTag());
        System.out.println(urlaub.getVon());
        System.out.println(urlaub.getBis());

        return "redirect:/student";
    }

    @Secured("ROLE_USER")
    @GetMapping("/klausurAnlegen")
    public String klausurAnlegen(Model model) {
        model.addAttribute("klausur", new Klausur(null, 0, false, null, null, null));
        return "NeueKlausur";
    }

    @Secured("ROLE_USER")
    @PostMapping("/klausurErstellen")
    public String klausurErstellen(@ModelAttribute Klausur klausur, Model model) {
        // TODO Anlegen der Klausur
        System.out.println(klausur.getVeranstaltung());
        System.out.println(klausur.getLsfid());
        System.out.println(klausur.isPraesenz());
        System.out.println(klausur.getDate());
        System.out.println(klausur.getBeginn());
        System.out.println(klausur.getEnd());

        return "redirect:/klausur";
    }

    @Secured("ROLE_USER")
    @GetMapping("/klausur")
    public String klausur() {
        return "Klausur";
    }

    @Secured("ROLE_USER")
    @PostMapping("/klausurAnmelden")
    public String klausurAnmelden() {
        return "redirect:/student";
    }


    @RequestMapping("/user")
    @ResponseBody
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttributes();
    }

    @GetMapping("/tokeninfo")
    @ResponseBody
    public Map<String, Object> tokeninfo(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
        OAuth2AccessToken gitHubAccessToken = authorizedClient.getAccessToken();
        return Map.of("token", gitHubAccessToken);
    }

    //TODO Am Ende die zwei Mappings wieder löschen

    @Secured("ROLE_ORGA")
    @GetMapping("/orga")
    public String orga() {
        return "Orga";
    }

    @Secured("ROLE_TUTOR")
    @GetMapping("/tutor")
    public String tutor() {
        return "Tutor";
    }

}