package de.propra.chicken.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String klausuranlage(){
        return "NeueKlausur";
    }
}
