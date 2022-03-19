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
        doNothing().when(klausurRepo).speicherKlausur(any());

        assertDoesNotThrow(() ->
            service.speicherKlausur(new Klausur("Test", 225282, false, LocalDate.now().toString(), LocalTime.now().toString(), LocalTime.now().plusHours(1).toString())));
    }

    @Test
    @DisplayName("Klausuranmeldung ")
    public void klausurAnmeldungTest() {

    }

    @Test
    @DisplayName("zwei überschneidende Urlaubsblöcke werden gemerged")
    void urlaubsanmeldungZweiUrlaubsbloeckeUeberschneidend() {
        StudentRepository studentRepo = mock(StudentRepository.class);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        StudentService studentService = new StudentService();
        KlausurService klausurService = mock(KlausurService.class);
        Service service = new Service(studentRepo, klausurRepo, studentService, klausurService);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub("1000-01-01", "10:00", "11:00");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "10:30", "11:30");
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        Set<Urlaub> gueltigeUrlaube = new HashSet<>();

        gueltigeUrlaube = service.ueberschneidendenUrlaubMergen(urlaube);
        Urlaub result = new Urlaub("1000-01-01", "10:00", "11:30");

        assertThat(gueltigeUrlaube).hasSize(1);
        assertThat(gueltigeUrlaube).contains(result);
    }

    @Test
    @DisplayName("zwei Urlaubsblöcke direkt hintereinander werden gemerged")
    void urlaubsanmeldungZweiUrlaubsbloeckeHintereinander() {
        StudentRepository studentRepo = mock(StudentRepository.class);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        StudentService studentService = new StudentService();
        KlausurService klausurService = mock(KlausurService.class);
        Service service = new Service(studentRepo, klausurRepo, studentService, klausurService);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub("1000-01-01", "10:00", "11:00");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "11:00", "12:00");
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        Set<Urlaub> gueltigeUrlaube = new HashSet<>();

        gueltigeUrlaube = service.ueberschneidendenUrlaubMergen(urlaube);
        Urlaub result = new Urlaub("1000-01-01", "10:00", "12:00");

        assertThat(gueltigeUrlaube).hasSize(1);
        assertThat(gueltigeUrlaube).contains(result);
    }

    @Test
    @DisplayName("zwei Urlaubsblöcke überschneiden sich, der eine enthält den anderen komplett")
    void urlaubsanmeldungZweiUrlaubsbloeckeEinerLaenger() {
        StudentRepository studentRepo = mock(StudentRepository.class);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        StudentService studentService = new StudentService();
        KlausurService klausurService = mock(KlausurService.class);
        Service service = new Service(studentRepo, klausurRepo, studentService, klausurService);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub("1000-01-01", "10:00", "11:00");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "10:00", "12:00");
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        Set<Urlaub> gueltigeUrlaube = new HashSet<>();

        gueltigeUrlaube = service.ueberschneidendenUrlaubMergen(urlaube);
        Urlaub result = new Urlaub("1000-01-01", "10:00", "12:00");

        assertThat(gueltigeUrlaube).hasSize(1);
        assertThat(gueltigeUrlaube).contains(result);
    }

    @Test
    @DisplayName("zwei Urlaubsblöcke überschneiden sich nicht")
    void urlaubsanmeldungZweiUrlaubsbloeckeKeineUeberschneidung() {
        StudentRepository studentRepo = mock(StudentRepository.class);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        StudentService studentService = new StudentService();
        KlausurService klausurService = mock(KlausurService.class);
        Service service = new Service(studentRepo, klausurRepo, studentService, klausurService);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub("1000-01-01", "10:00", "11:00");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "11:30", "12:00");
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        Set<Urlaub> gueltigeUrlaube = new HashSet<>();

        gueltigeUrlaube = service.ueberschneidendenUrlaubMergen(urlaube);
        Urlaub result = new Urlaub("1000-01-01", "10:00", "11:00");
        Urlaub result2 = new Urlaub("1000-01-01", "11:30", "12:00");

        assertThat(gueltigeUrlaube).hasSize(2);
        assertThat(gueltigeUrlaube).contains(result, result2);
    }

    @Test
    @DisplayName("drei Urlaubsblöcke direkt nacheinander zu einem Block")
    void urlaubsanmeldungDreiUrlaubsbloeckeNacheinander() {
        StudentRepository studentRepo = mock(StudentRepository.class);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        StudentService studentService = new StudentService();
        KlausurService klausurService = mock(KlausurService.class);
        Service service = new Service(studentRepo, klausurRepo, studentService, klausurService);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub("1000-01-01", "08:30", "08:45");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "08:45", "09:30");
        Urlaub urlaub3 = new Urlaub("1000-01-01", "09:30", "10:00");
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        urlaube.add(urlaub3);
        Set<Urlaub> gueltigeUrlaube = new HashSet<>();

        gueltigeUrlaube = service.ueberschneidendenUrlaubMergen(urlaube);
        Urlaub result = new Urlaub("1000-01-01", "08:30", "10:00");

        assertThat(gueltigeUrlaube).hasSize(1);
        assertThat(gueltigeUrlaube).contains(result);
    }

    @Test
    @DisplayName("drei Urlaubsblöcke, keine Überschneidungen")
    void urlaubsanmeldungDreiUrlaubsbloeckeKeineUeberschneidung() {
        StudentRepository studentRepo = mock(StudentRepository.class);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        StudentService studentService = new StudentService();
        KlausurService klausurService = mock(KlausurService.class);
        Service service = new Service(studentRepo, klausurRepo, studentService, klausurService);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub("1000-01-01", "08:30", "09:30");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "09:45", "10:00");
        Urlaub urlaub3 = new Urlaub("1000-01-01", "11:00", "11:30");
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        urlaube.add(urlaub3);
        Set<Urlaub> gueltigeUrlaube;

        gueltigeUrlaube = service.ueberschneidendenUrlaubMergen(urlaube);
        Urlaub result = new Urlaub("1000-01-01", "08:30", "09:30");
        Urlaub result2 = new Urlaub("1000-01-01", "09:45", "10:00");
        Urlaub result3 = new Urlaub("1000-01-01", "11:00", "11:30");

        assertThat(gueltigeUrlaube).hasSize(3);
        assertThat(gueltigeUrlaube).contains(result, result2, result3);
    }

    @Test
    @DisplayName("vier Urlaubsblöcke, je zwei überschneiden sich")
    void urlaubsanmeldungVierUrlaubsbloeckeZweiUeberschneidungen() {
        StudentRepository studentRepo = mock(StudentRepository.class);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        StudentService studentService = new StudentService();
        KlausurService klausurService = mock(KlausurService.class);
        Service service = new Service(studentRepo, klausurRepo, studentService, klausurService);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub("1000-01-01", "08:30", "09:30");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "09:00", "10:00");
        Urlaub urlaub3 = new Urlaub("1000-01-01", "11:00", "11:30");
        Urlaub urlaub4 = new Urlaub("1000-01-01", "11:15", "12:00");
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        urlaube.add(urlaub3);
        urlaube.add(urlaub4);
        Set<Urlaub> gueltigeUrlaube;

        gueltigeUrlaube = service.ueberschneidendenUrlaubMergen(urlaube);
        Urlaub result = new Urlaub("1000-01-01", "08:30", "10:00");
        Urlaub result2 = new Urlaub("1000-01-01", "11:00", "12:00");

        assertThat(gueltigeUrlaube).hasSize(2);
        assertThat(gueltigeUrlaube).contains(result, result2);
    }

}


