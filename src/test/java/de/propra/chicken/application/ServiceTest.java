package de.propra.chicken.application;

import de.propra.chicken.application.service.Service;
import de.propra.chicken.application.service.repo.KlausurRepository;
import de.propra.chicken.application.service.repo.StudentRepository;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.service.KlausurService;
import de.propra.chicken.domain.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ServiceTest {

    @Test
    @DisplayName("Testet ob eine ungueltige LSF ID einen Fehler wirft")
    public void invalidLSFID(){
        //TODO: (in)validLsfID aus DB oder Internet je eigene Methode
        StudentRepository studentRepo = mock(StudentRepository.class);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        KlausurService klausurService = new KlausurService();
        StudentService studentService = mock(StudentService.class);
        Service service = new Service(studentRepo, klausurRepo,studentService, klausurService);
        doNothing().when(klausurRepo).speicherKlausur(any());

        assertThrows(Exception.class, () -> {
            service.speicherKlausur(new Klausur("Test", 123, false, null, null, null));
        });
    }

    @Test
    @DisplayName("Testet ob eine gueltige LSF ID keinen Fehler wirft")
    public void validLSFID(){
        StudentRepository studentRepo = mock(StudentRepository.class);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        KlausurService klausurService = new KlausurService();
        StudentService studentService = mock(StudentService.class);
        Service service = new Service(studentRepo, klausurRepo, studentService, klausurService);
        doNothing().when(klausurRepo).speicherKlausur(any());

        assertDoesNotThrow(() ->
            service.speicherKlausur(new Klausur("Test", 12345, false, null, null, null)));
    }

    @Test
    @DisplayName("Klausuranmeldung ")
    public void klausurAnmeldungTest() {

    }



}


