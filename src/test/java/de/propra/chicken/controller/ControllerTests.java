package de.propra.chicken.controller;


import de.propra.chicken.application.service.Service;
import de.propra.chicken.configuration.MethodSecurityConfiguration;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;
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
import static org.mockito.Mockito.*;
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

    OAuth2User buildOAuth2User(String role, String name) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("login", name);
        attributes.put("id", 12345);

        List<GrantedAuthority> authorities = Collections.singletonList(
                new OAuth2UserAuthority("ROLE_" + role.toUpperCase(), attributes));
        return new DefaultOAuth2User(authorities, attributes, "login");
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

    @Test
    @DisplayName("Orga Mitglieder können auf die Orga Seite zugreifen")
    void orga() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("orga", "Max Mustermann");
        MockHttpSession newSession = new MockHttpSession();
        newSession.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, new SecurityContextImpl(principal));

        session = newSession;

        mockMvc.perform(get("/orga").session(session)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("Orga"));

    }

    @Test
    @DisplayName("Tutoren können auf die Tutor Seite zugreifen")
    void tutor() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("tutor", "Max Mustermann");
        MockHttpSession newSession = new MockHttpSession();
        newSession.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, new SecurityContextImpl(principal));

        session = newSession;

        mockMvc.perform(get("/tutor").session(session)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("Tutor"));

    }

    @Test
    @DisplayName("Nicht-Orga Mitglieder können nicht auf die Orga Seite zugreifen")
    void nonOrga() throws Exception {
        mockMvc.perform(get("/orga").session(session)
                        .with(csrf()))
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("Nicht-Tutoren können nicht auf die Tutor Seite zugreifen")
    void nonTutor() throws Exception {
        mockMvc.perform(get("/tutor").session(session)
                        .with(csrf()))
                .andExpect(status().isForbidden());

    }


    @Test
    @DisplayName("Prueft dass die Service Methoden bei einer Klausuranmeldung richtig aufgerufen werden")
    void klausuranmeldenService() throws Exception {
        OAuth2User oAuth2User = buildOAuth2User("user", "Max Mustermann");
        mockMvc.perform(post("/klausurAnmelden").session(session)
                        .param("id", "12345")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"));

        verify(service).klausurAnmeldung(12345L, oAuth2User);


    }

    @Test
    @DisplayName("Prueft dass die Service Methoden bei einer Klausur Erstellung richtig aufgerufen werden")
    void klausuranlegenService() throws Exception {
        Klausur klausur = new Klausur("test", 12345L, true, LocalDate.now().toString(), LocalTime.now().toString(), LocalTime.now().plusHours(1).toString());
        OAuth2User oAuth2User = buildOAuth2User("user", "Max Mustermann");
        mockMvc.perform(post("/klausurErstellen").session(session)
                        .param("name", klausur.getName())
                        .param("_praesenz", "on")
                        .param("lsfid", String.valueOf(klausur.getLsfid()))
                        .param("datum", klausur.getDatum().toString())
                        .param("beginn", klausur.getBeginn().toString())
                        .param("ende", klausur.getEnde().toString())
                        .with(csrf()))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/klausur"));

        verify(service).saveKlausur(klausur, oAuth2User);
    }

    @Test
    @DisplayName("Prueft ob die Service methoden fuers erstellen eines Urlaubs richtig aufgerufen werden")
    void urlaubPostService() throws Exception {
        OAuth2User oAuth2User = buildOAuth2User("user", "Max Mustermann");
        Urlaub urlaub = new Urlaub(LocalDate.now().toString(), LocalTime.now().toString(), LocalTime.now().plusHours(1).toString());
        mockMvc.perform(post("/urlaubErstellen").session(session)
                        .param("tag", urlaub.getTag().toString())
                        .param("beginn", urlaub.getBeginn().toString())
                        .param("end", urlaub.getEnd().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"));

        verify(service).speicherUrlaub(urlaub, 12345, oAuth2User);
    }

    @Test
    @DisplayName("Prüft das die Klausurstornierung richtig aufgerufen wird")
    void klausurStornieren() throws Exception {
        OAuth2User user = buildOAuth2User("user", "Max Mustermann");
        mockMvc.perform(post("/klausurStornieren").session(session)
                .param("ref", "12345")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"));

        verify(service).storniereKlausurAnmeldung(12345, user);

    }

    @Test
    @DisplayName("Prüft das die Urlaubsstornierung richtig aufgerufen wird")
    void urlaubStornieren() throws Exception {
        OAuth2User user = buildOAuth2User("user", "Max Mustermann");
        Urlaub urlaub = new Urlaub(LocalDate.now().toString(), LocalTime.now().toString(), LocalTime.now().toString());
        mockMvc.perform(post("/urlaubStornieren").session(session)
                        .param("tag", urlaub.getTag().toString())
                        .param("beginn", urlaub.getBeginn().toString())
                        .param("end", urlaub.getEnd().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"));

        verify(service).urlaubStornieren(user, urlaub.getTag().toString(), urlaub.getBeginn().toString(), urlaub.getEnd().toString());
    }

    @Test
    @DisplayName("/ wird auf /student weitergeleitet")
    void index() throws Exception {
        mockMvc.perform(get("/").session(session)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"));
    }


}