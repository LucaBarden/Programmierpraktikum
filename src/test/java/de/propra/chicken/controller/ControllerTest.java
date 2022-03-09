package de.propra.chicken.controller;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class ControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Prüft ob Student.html auf /student angezeigt wird")
    void student() throws Exception {
        mockMvc.perform(get("/student"))
                .andExpect(status().isOk())
                .andExpect(view().name("Student"));
    }

    @Test
    @DisplayName("Prüft ob Urlaub.html auf /urlaubanlage angezeigt wird")
    void urlaub() throws Exception {
        mockMvc.perform(get("/urlaubanlage"))
                .andExpect(status().isOk())
                .andExpect(view().name("NeuerUrlaub"));
    }

    @Test
    @DisplayName("Prüft ob Klausur.html auf /klausuranmeldung angezeigt wird")
    void klausuranmeldung() throws Exception {
        mockMvc.perform(get("/klausuranmeldung"))
                .andExpect(status().isOk())
                .andExpect(view().name("KlausurAnmeldung"));
    }

    @Test
    @DisplayName("Prüft ob NeueKlausur.html auf /klausuranlage angezeigt wird")
    void klausuranlage() throws Exception {
        mockMvc.perform(get("/klausuranlage"))
                .andExpect(status().isOk())
                .andExpect(view().name("NeueKlausur"));
    }

    @Test
    @DisplayName("Button auf /urlauberstellen leitet auf /student weiter")
    void urlaubPost() throws Exception{
        mockMvc.perform(post("/urlauberstellen"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"));
    }

    @Test
    @Disabled
    @DisplayName("Button auf /klausurerstellen leitet auf /klausuranmeldung")
    void klausuranlegen() throws Exception {
        mockMvc.perform(post("/klausurerstellen"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/klausuranmeldung"));
    }

    @Test
    @DisplayName("Button auf /klausuranmelden leitet auf /student weiter")
    void klausuranmelden() throws  Exception {
        mockMvc.perform(post("/klausuranmelden"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"));
    }



}
