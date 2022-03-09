package de.propra.chicken.controller;

import de.propra.chicken.domain.model.Urlaub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.when;
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
    @DisplayName("Prüft ob NeuerUrlaub.html auf /urlaubanlage angezeigt wird")
    void urlaub() throws Exception {
        mockMvc.perform(get("/urlaubanlage"))
                .andExpect(status().isOk())
                .andExpect(view().name("NeuerUrlaub"));
    }

    @Test
    @DisplayName("Prüft ob KlausurAnmeldung.html auf /klausuranmeldung angezeigt wird")
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
    @DisplayName("Button auf /urlaubanlage leitet auf /student weiter")
    void urlaubPost() throws Exception{
        mockMvc.perform(post("/urlaubanlegen"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"));
    }

    @Test
    @DisplayName("Button auf /klausuranlage leitet auf /klausuranmeldung weiter")
    void addroomPost() throws Exception{
        mockMvc.perform(post("/klausuranlegen"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/klasuranmeldung"));
    }


}
