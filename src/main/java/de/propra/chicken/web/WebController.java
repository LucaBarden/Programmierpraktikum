/*
package de.propra.chicken.web;

import de.propra.chicken.domain.model.Klausur;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
public class WebController {

    @GetMapping("/")
    public String klausuranlage(Model model){
        model.addAttribute("formular", new Klausur(null, null, false, null, null, null));
        return "NeueKlausur.html";
    }

    @PostMapping("/anlegen")
    public String anlegen(Klausur klausur, BindingResult errors, Model model) {
        model.addAttribute("formular", klausur);
        System.out.println(klausur.getVeranstaltung());
        System.out.println(klausur.getBeginn());
        System.out.println(klausur.getEnd());
        System.out.println(klausur.getLsfid());
        System.out.println(klausur.getDate());
        System.out.println(klausur.isPraesenz());

        return "redirect:/";
    }
}
*/
