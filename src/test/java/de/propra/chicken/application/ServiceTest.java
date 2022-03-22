package de.propra.chicken.application;

import de.propra.chicken.application.service.Service;
import de.propra.chicken.application.service.repo.KlausurRepository;
import de.propra.chicken.application.service.repo.StudentRepository;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Urlaub;
import de.propra.chicken.domain.service.KlausurService;
import de.propra.chicken.domain.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServiceTest {

    @Test
    @DisplayName("Testet ob eine ungueltige LSF ID einen Fehler wirft")
    public void invalidLSFID(){

        StudentRepository studentRepo = mock(StudentRepository.class);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        KlausurService klausurService = new KlausurService();
        StudentService studentService = mock(StudentService.class);
        Service service = new Service(studentRepo, klausurRepo,studentService, klausurService);
        try {
            when(klausurRepo.speicherKlausur(any())).thenReturn(null);
        }
        catch(Exception ex) {
            fail();
        }

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.speicherKlausur(new Klausur("Test", 123, false, LocalDate.now().toString(), LocalTime.now().toString(), LocalTime.now().plusHours(1).toString()));
        });
        assertThat(exception.getMessage()).isEqualTo("Invalide LSF ID");
    }

    @Test
    @DisplayName("Testet ob eine gueltige LSF ID keinen Fehler wirft")
    public void validLSFID(){
        StudentRepository studentRepo = mock(StudentRepository.class);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        KlausurService klausurService = new KlausurService();
        StudentService studentService = mock(StudentService.class);
        Service service = new Service(studentRepo, klausurRepo, studentService, klausurService);
        try {
            when(klausurRepo.speicherKlausur(any())).thenReturn(null);
        }
        catch(Exception ex) {
            fail();
        }

        assertDoesNotThrow(() ->
            service.speicherKlausur(new Klausur("Test", 225282, false, LocalDate.now().plusDays(2).toString(), LocalTime.now().toString(), LocalTime.now().plusHours(1).toString())));
    }

    @Test
    @DisplayName("Klausuranmeldung ")
    public void klausurAnmeldungTest() {

    }

}


