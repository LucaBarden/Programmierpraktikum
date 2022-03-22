package de.propra.chicken.controller;


import de.propra.chicken.application.service.Service;
import de.propra.chicken.configuration.MethodSecurityConfiguration;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc()
@WebMvcTest(controllers = WebController.class)
@Import({MethodSecurityConfiguration.class})
public class ControllerTests {

    private static MockHttpSession session;

    @MockBean
    Service service;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    private void loggedInUser() {
        OAuth2AuthenticationToken principal = buildPrincipal("user", "Max Mustermann");
        MockHttpSession newSession = new MockHttpSession();
        newSession.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, new SecurityContextImpl(principal));

        session = newSession;
    }


    OAuth2AuthenticationToken buildPrincipal(String role, String name) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("login", name);
        attributes.put("id", 12345);

        List<GrantedAuthority> authorities = Collections.singletonList(
                new OAuth2UserAuthority("ROLE_" + role.toUpperCase(), attributes));
        OAuth2User user = new DefaultOAuth2User(authorities, attributes, "login");
        return new OAuth2AuthenticationToken(user, authorities, "whatever");
    }

    @Test
    @DisplayName("Prüft ob ein eingeloggter Nutzer /Student angezeigt bekommt")
    void student() throws Exception {
        when(service.findStudentByGithubID(12345)).thenReturn(new Student(12345));
        mockMvc.perform(get("/student")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("Student"));
    }

    @Test
    @DisplayName("Prüft ob Urlaub.html auf /Urlaub angezeigt wird")
    void urlaub() throws Exception {
        mockMvc.perform(get("/urlaub").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("Urlaub"));
    }

    @Test
    @DisplayName("Prüft ob Klausur.html auf /klausur angezeigt wird")
    void klausuranmeldung() throws Exception {
        mockMvc.perform(get("/klausur").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("Klausur"));
    }

    @Test
    @DisplayName("Prüft ob NeueKlausur.html auf /klausurAnlegen angezeigt wird")
    void klausuranlage() throws Exception {
        mockMvc.perform(get("/klausurAnlegen").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("NeueKlausur"));
    }

    @Test
    @DisplayName("Button auf /urlaub leitet auf /student weiter")
    void urlaubPost() throws Exception {
        doNothing().when(service).speicherUrlaub(any(), anyLong(), any());
        mockMvc.perform(post("/urlaubErstellen").session(session)
                        .param("tag", LocalDate.now().toString())
                        .param("beginn", LocalTime.now().toString())
                        .param("end", LocalTime.now().plusHours(1).toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"));
    }

    @Test
    @DisplayName("Button auf der Seite /klausurAnlegen leitet auf /klausur weiter")
    void klausuranlegen() throws Exception {
        doNothing().when(service).speicherKlausur(any());
        mockMvc.perform(post("/klausurErstellen").session(session)
                        .param("name", "test")
                        .param("_praesenz", "on")
                        .param("lsfid", "12345")
                        .param("datum", LocalDate.now().toString())
                        .param("beginn", LocalTime.now().toString())
                        .param("ende", LocalTime.now().plusHours(1).toString())
                        .with(csrf()))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/klausur"));
    }

    @Test
    @DisplayName("Button auf der Seite /klausur leitet auf /student weiter")
    void klausuranmelden() throws Exception {
        doNothing().when(service).klausurAnmeldung(anyLong(), any());
        mockMvc.perform(post("/klausurAnmelden").session(session)
                        .param("id", "12345")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"));
    }

}