package de.propra.chicken.controller;


import de.propra.chicken.configuration.MethodSecurityConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Import({MethodSecurityConfiguration.class})
public class SecuredControllerTests {

    private static MockHttpSession session;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    private void loggedInUser() {
        OAuth2AuthenticationToken principal = buildPrincipal("user", "Luca Barden");
        MockHttpSession newSession = new MockHttpSession();
        newSession.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, new SecurityContextImpl(principal));

        session = newSession;
    }

    OAuth2AuthenticationToken buildPrincipal(String role, String name) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("login", name);

        List<GrantedAuthority> authorities = Collections.singletonList(
                new OAuth2UserAuthority("ROLE_" + role.toUpperCase(), attributes));
        OAuth2User user = new DefaultOAuth2User(authorities, attributes, "login");
        return new OAuth2AuthenticationToken(user, authorities, "whatever");
    }

    @Test
    @DisplayName("Prüft ob ein eingeloggter Nutzer /Student angezeigt bekommt")
    void student() throws Exception {
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
    @DisplayName("Button auf /urlaub leitet auf /Student weiter")
    void urlaubPost() throws Exception {
        mockMvc.perform(post("/urlaub").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"));
    }

    @Test
    @DisplayName("Button auf /klausurErstellen leitet auf /klausur")
    void klausuranlegen() throws Exception {
        mockMvc.perform(post("/klausurErstellen").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/klausur"));
    }

    @Test
    @DisplayName("Button auf /klausur leitet auf /Student weiter")
    void klausuranmelden() throws Exception {
        mockMvc.perform(post("/klausur").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student"));
    }


}
