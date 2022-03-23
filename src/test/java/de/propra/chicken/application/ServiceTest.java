package de.propra.chicken.application;

import de.propra.chicken.application.service.Service;
import de.propra.chicken.application.service.repo.KlausurRepository;
import de.propra.chicken.application.service.repo.StudentRepository;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;
import de.propra.chicken.domain.service.KlausurService;
import de.propra.chicken.domain.service.StudentService;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServiceTest {

    private static final String BEGINN_PRAKTIKUM = "08:30";
    private static final String ENDE_PRAKTIKUM   = "12:30";
    private static final String STARTDATUM = LocalDate.now().minusDays(7).toString();
    private static final String ENDDATUM   = LocalDate.now().plusDays(7).toString();

    static Service service;
    static StudentRepository studentRepo;
    static KlausurRepository klausurRepo;
    static KlausurService klausurService;
    static StudentService studentService;

    @BeforeAll
    static void beforeAll() {
        studentRepo = mock(StudentRepository.class);
        klausurRepo = mock(KlausurRepository.class);
        klausurService = mock(KlausurService.class);
        studentService = mock(StudentService.class);
        service = new Service(studentRepo, klausurRepo,studentService, klausurService);
    }


    @Test
    @DisplayName("Testet ob eine ungueltige LSF ID einen Fehler wirft")
    public void invalidLSFID() throws Exception {

        when(klausurRepo.speicherKlausur(any())).thenReturn(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.speicherKlausur(new Klausur("Test", 123, false, LocalDate.now().toString(), LocalTime.now().toString(), LocalTime.now().plusHours(1).toString()));
        });
        assertThat(exception.getMessage()).isEqualTo("Invalide LSF ID");
    }

    @Test
    @DisplayName("Testet ob eine gueltige LSF ID keinen Fehler wirft")
    public void validLSFID() throws Exception {

        when(klausurRepo.speicherKlausur(any())).thenReturn(null);
        assertDoesNotThrow(() ->
            service.speicherKlausur(new Klausur("Test", 225282, false, LocalDate.now().plusDays(2).toString(), BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM)));
    }

    @Test
    @DisplayName("Klausuranmeldung wird korrekt aufgerufen")
    public void klausurAnmeldungTest() throws Exception {
        Student student = mock(Student.class);
        Klausur klausur = new Klausur("RA", 65464, true, "1999-01-01", "08:30", "09:30");
        OAuth2User user = mock(OAuth2User.class);
        when(klausurRepo.findeKlausurByID(anyLong())).thenReturn(klausur);
        when(studentRepo.findByID(anyLong())).thenReturn(student);
        when(user.getAttribute(anyString())).thenReturn(anyLong());

        service.klausurAnmeldung(45642, user);

        verify(studentService, times(1)).validiereKlausurAnmeldung(klausur, student, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        verify(student, times(1)).aendereUrlaube(new HashSet<>(), LocalDate.of(1999,1,1));
        verify(student, times(1)).addKlausur(any());
        verify(studentRepo, times(1)).speicherStudent(student);
    }

    @Test
    @DisplayName("Klausur speichern wird korrekt aufgerufen")
    public void saveKlausur() {
    }
}


