package de.propra.chicken.controller;

import de.propra.chicken.application.service.Service;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;
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

import java.time.LocalDate;
import java.util.Map;


@Controller
public class WebController {

    private final Service service;

    public WebController(Service service) {
        this.service = service;
    }

    @Secured("ROLE_USER")
    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("user",
                principal != null ? principal.getAttribute("login") : null
        );
        System.out.println(principal.getAttributes());
        return "redirect:/student";
    }

    @Secured("ROLE_USER")
    @GetMapping("/student")
    public String student(Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("user",
                principal != null ? principal.getAttribute("login") : null
        );
        service.speicherStudent(new Student(Long.parseLong(principal.getAttribute("id").toString())));
        return "Student";
    }

    @Secured("ROLE_USER")
    @GetMapping("/urlaub")
    public String urlaub(Model model) {
        model.addAttribute("urlaub", new Urlaub(LocalDate.now().toString(), "08:30", "12:00"));
        return "Urlaub";
    }

    @Secured("ROLE_USER")
    @PostMapping("/urlaubErstellen")
    public String urlaubErstellen(Urlaub urlaub, @AuthenticationPrincipal OAuth2User principal) {
        System.out.println(urlaub);
        try {
            service.speicherUrlaub(urlaub, Long.parseLong(principal.getAttribute("id").toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/student";
    }

    @Secured("ROLE_USER")
    @GetMapping("/klausurAnlegen")
    public String klausurAnlegen(Model model) {
        model.addAttribute("klausur", new Klausur(null, 0, false, LocalDate.now().toString(), "08:30", "12:00"));
        return "NeueKlausur";
    }

    @Secured("ROLE_USER")
    @PostMapping("/klausurErstellen")
    public String klausurErstellen(Klausur klausur) {
        System.out.println(klausur);
        try {
            service.saveKlausur(klausur);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    //TODO security ändern

    @Secured("ROLE_USER")
    @GetMapping("/orga")
    public String orga() {
        return "Orga";
    }

    @Secured("ROLE_USER")
    @GetMapping("/tutor")
    public String tutor() {
        return "Tutor";
    }

}