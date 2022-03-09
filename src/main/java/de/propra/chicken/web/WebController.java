package de.propra.chicken.web;

import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Urlaub;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "Student";
    }

    @GetMapping("/student")
    public String student() {
        return "Student";
    }


    @GetMapping("/urlaubanlage")
    public String urlaubanlage(Model model) {
        model.addAttribute("urlaub", new Urlaub(null, null, null));
        return "NeuerUrlaub";
    }

    @PostMapping("/urlauberstellen")
    public String urlaubAnlegen(@ModelAttribute Urlaub urlaub, Model model) {
        model.addAttribute("urlaub", urlaub);

        // TO-DO Anlegen des Urlaubs

        System.out.println(urlaub.getTag());
        System.out.println(urlaub.getVon());
        System.out.println(urlaub.getBis());

        return "redirect:/student";
    }


    @GetMapping("/klausuranlage")
    public String klausuranlage(Model model) {
        model.addAttribute("klausur", new Klausur(null, 0, false, null, null, null));
        return "NeueKlausur";
    }

    @PostMapping("/klausurerstellen")
    public String klausurAnlegen(@ModelAttribute Klausur klausur, Model model) {
        model.addAttribute("klausur", klausur);

        // TO-DO Anlegen der Klausur

        System.out.println(klausur.getVeranstaltung());
        System.out.println(klausur.getBeginn());
        System.out.println(klausur.getEnd());
        System.out.println(klausur.getLsfid());
        System.out.println(klausur.getDate());
        System.out.println(klausur.isPraesenz());

        return "redirect:/klausuranmeldung";
    }

    @GetMapping("/klausuranmeldung")
    public String klausurAnmeldung() {
        return "KlausurAnmeldung";
    }

    @PostMapping("/klausuranmelden")
    public String klausurAnmelden() {
        return "redirect:/student";
    }
}