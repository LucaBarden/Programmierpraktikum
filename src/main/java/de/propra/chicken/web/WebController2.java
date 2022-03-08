package de.propra.chicken.web;

import de.propra.chicken.domain.model.FormularTest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
public class WebController2 {

    @GetMapping("/")
    public String klausuranlage(Model model){
        model.addAttribute("formular", new FormularTest("test", 0, true, 0, 0, 0));
        return "NeueKlausur.html";
    }

    @ResponseBody
    @PostMapping("/anlegen")
    public String anlegen(FormularTest formular, BindingResult errors, Model model) {
        model.addAttribute("formular", formular);
        System.out.println(formular.getVeranstaltung());
        System.out.println(formular.getBeginn());
        System.out.println(formular.getEnd());
        System.out.println(formular.getLsfid());
        System.out.println(formular.getDate());
        System.out.println(formular.isPraesenz());

        return "NeueKlausur.html";
    }
}