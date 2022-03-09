package de.propra.chicken.web;

import de.propra.chicken.domain.model.Klausur;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class WebController {

    @GetMapping("/")
    public String klausuranlage(Model model){
        model.addAttribute("klausur", new Klausur(null, 0, false, null, null, null));
        return "NeueKlausur.html";
    }

    @PostMapping("/anlegen")
    public String anlegen(@ModelAttribute Klausur klausur, Model model) {
        model.addAttribute("klausur", klausur);

        // TO-DO Anlegen der KLausur

        System.out.println(klausur.getVeranstaltung());
        System.out.println(klausur.getBeginn());
        System.out.println(klausur.getEnd());
        System.out.println(klausur.getLsfid());
        System.out.println(klausur.getDate());
        System.out.println(klausur.isPraesenz());

        return "redirect:/";
    }
}