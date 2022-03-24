package de.propra.chicken.controller;

import de.propra.chicken.application.service.Service;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Objects;


@Controller
public class WebController {

    private final Service service;
    public WebController(Service service) {
        this.service = service;
    }

    @Secured("ROLE_USER")
    @GetMapping("/")
    public String index(@AuthenticationPrincipal OAuth2User principal) {
        return "redirect:/student";
    }

    @Secured("ROLE_USER")
    @GetMapping("/student")
    public String student(Model model, @AuthenticationPrincipal OAuth2User principal) {
        service.speicherStudent(new Student(Long.parseLong(Objects.requireNonNull(principal.getAttribute("id")).toString())));
        Student student = null;
        try {
            student = service.findStudentByGithubID(Long.parseLong(Objects.requireNonNull(principal.getAttribute("id")).toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("student", student);
        if (student != null) {
            model.addAttribute("klausuren", service.ladeAngemeldeteKlausuren(student.getGithubID()));
        }
        try {
            if (student != null) {
                model.addAttribute("urlaube", service.ladeAngemeldeteUrlaube(student.getGithubID()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Student";
    }

    @Secured("ROLE_USER")
    @GetMapping("/urlaub")
    public String urlaub(Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("urlaub", new Urlaub(LocalDate.now().toString(), "08:30", "12:30"));
        service.speicherStudent(new Student(Long.parseLong(Objects.requireNonNull(principal.getAttribute("id")).toString())));
        return "Urlaub";
    }

    @Secured("ROLE_USER")
    @PostMapping("/urlaubErstellen")
    public String urlaubErstellen(Urlaub urlaub, @AuthenticationPrincipal OAuth2User principal, Model model) {
        try {
            service.speicherUrlaub(urlaub, Long.parseLong(Objects.requireNonNull(principal.getAttribute("id")).toString()), principal.getAttribute("login"));
        } catch (Exception e) {
            model.addAttribute("error", true);
            model.addAttribute("errortxt", e.getMessage());
            return urlaub(model, principal);
        }

        return "redirect:/student";
    }

    @Secured("ROLE_USER")
    @PostMapping("urlaubStornieren")
    public String urlaubStornieren(String tag, String beginn, String end, @AuthenticationPrincipal OAuth2User principal) {
        try {
            service.urlaubStornieren(principal.getAttribute("login"),Long.parseLong(Objects.requireNonNull(principal.getAttribute("id")).toString()), tag, beginn, end);
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
    public String klausurErstellen(Model model, Klausur klausur, @AuthenticationPrincipal OAuth2User principal) {
        System.out.println(klausur);
        try {
            service.saveKlausur(klausur, principal.getAttribute("login"), Long.parseLong(Objects.requireNonNull(principal.getAttribute("id")).toString()) );
        } catch (Exception e) {
            model.addAttribute("error", true);
            model.addAttribute("errortxt", e.getMessage());
            return klausurAnlegen(model);
        }
        return "redirect:/klausur";
    }

    @Secured("ROLE_USER")
    @GetMapping("/klausur")
    public String klausur(Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("klausuren", service.ladeAlleKlausuren());
        service.speicherStudent(new Student(Long.parseLong(Objects.requireNonNull(principal.getAttribute("id")).toString())));
        return "Klausur";
    }

    @Secured("ROLE_USER")
    @PostMapping("/klausurAnmelden")
    public String klausurAnmelden(Model model, long id, @AuthenticationPrincipal OAuth2User principal) {
        try {
            service.klausurAnmeldung(id, principal.getAttribute("login"), Long.parseLong(Objects.requireNonNull(principal.getAttribute("id")).toString()));
        } catch (Exception e) {
            model.addAttribute("error", true);
            model.addAttribute("errortxt", e.getMessage());
            return klausur(model, principal);
        }
        return "redirect:/student";
    }

    @Secured("ROLE_USER")
    @PostMapping("/klausurStornieren")
    public String klausurStornieren(@RequestParam("ref") long id, @AuthenticationPrincipal OAuth2User principal) {
        try {
            service.storniereKlausurAnmeldung(id, principal.getAttribute("login"), Long.parseLong(Objects.requireNonNull(principal.getAttribute("id")).toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/student";
    }

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