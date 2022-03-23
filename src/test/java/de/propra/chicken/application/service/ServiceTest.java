package de.propra.chicken.application.service;

import de.propra.chicken.application.service.repo.KlausurRepository;
import de.propra.chicken.application.service.repo.StudentRepository;
import de.propra.chicken.domain.model.*;
import de.propra.chicken.domain.service.KlausurService;
import de.propra.chicken.domain.service.StudentService;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.*;

public class ServiceTest {

    private static final Dotenv dotenv = Dotenv.load();

    private static String BEGINN     = dotenv.get("STARTUHRZEIT_PRAKTIKUM");
    private static String ENDE       = dotenv.get("ENDUHRZEIT_PRAKTIKUM");
    private static String STARTDATUM = dotenv.get("STARTDATUM_PRAKTIKUM");
    private static String ENDDATUM   = dotenv.get("ENDDATUM_PRAKTIKUM");

    Service service;
    StudentRepository studentRepo;
    KlausurRepository klausurRepo;
    KlausurService klausurService;
    StudentService studentService;
    Clock clock;

    @BeforeEach
    void beforeEach() {
        studentRepo = mock(StudentRepository.class);
        klausurRepo = mock(KlausurRepository.class);
        klausurService = mock(KlausurService.class);
        studentService = mock(StudentService.class);
        clock = mock(Clock.class);
        service = new Service(studentRepo, klausurRepo,studentService, klausurService, clock);
    }

    @Test
    @DisplayName("Klausuranmeldung wird korrekt aufgerufen")
    void klausurAnmeldungTest() throws Exception {
        Student student = mock(Student.class);
        Klausur klausur = new Klausur("RA", 65464, true, "1999-01-01", "08:30", "09:30");
        OAuth2User user = mock(OAuth2User.class);
        when(klausurRepo.findeKlausurByID(anyLong())).thenReturn(klausur);
        when(studentRepo.findByID(anyLong())).thenReturn(student);
        when(user.getAttribute(anyString())).thenReturn(anyLong());

        service.klausurAnmeldung(45642, user);

        verify(studentService, times(1)).validiereKlausurAnmeldung(klausur, student, BEGINN, ENDE);
        verify(student, times(1)).aendereUrlaube(new HashSet<>(), LocalDate.of(1999,1,1));
        verify(student, times(1)).addKlausur(any());
        verify(studentRepo, times(1)).speicherStudent(student);
    }

    @Test
    @DisplayName("Klausur speichern wird korrekt aufgerufen")
    void saveKlausur() throws Exception {
        OAuth2User user = mock(OAuth2User.class);
        Klausur klausur = mock(Klausur.class);
        when(klausurRepo.speicherKlausur(any())).thenReturn(klausur);
        doNothing().when(klausurService).validiereLsfIdInternet(klausur);

        service.saveKlausur(klausur, user);

        verify(klausurService, times(1)).validiereLsfIdInternet(klausur);
        verify(klausurService, times(1)).validiereKlausur(klausur, BEGINN, ENDE, STARTDATUM, ENDDATUM);

    }

    @Test
    @DisplayName("ladeAlleKlausuren wird korrekt aufgerufen")
    void ladeAlleKlausuren() {
        Klausur klausur = mock(Klausur.class);
        Set<Klausur> klausur1 = Set.of(klausur);
        when(klausurRepo.ladeAlleKlausuren()).thenReturn(klausur1);

        service.ladeAlleKlausuren();

        verify(klausurRepo, times(1)).ladeAlleKlausuren();
        verify(klausurService, times(1)).klausurIstNochImAnmeldezeitraum(klausur1);

    }

    @Test
    @DisplayName("Prueft ob die Klausur Stornierung richtig aufgerufen wird")
    void klausurStornieren() throws Exception {
        Klausur klausur = mock(Klausur.class);
        Student student = mock(Student.class);
        OAuth2User user = mock(OAuth2User.class);
        when(klausurRepo.findeKlausurByID(anyLong())).thenReturn(klausur);
        when(studentRepo.findByID(anyLong())).thenReturn(student);
        when(user.getAttribute(anyString())).thenReturn(anyLong());

        service.storniereKlausurAnmeldung(12345, user);

        verify(klausurRepo, times(1)).findeKlausurByID(anyLong());
        verify(studentRepo, times(1)).findByID(anyLong());
        verify(student, times(1)).entferneUrlaubeAnEinemTag(klausur.getDatum());
        verify(student, times(1)).entferneKlausur(anyLong());
        verify(studentRepo, times(1)).speicherStudent(student);
    }

    @Test
    @DisplayName("Prueft ob speicherUrlaub richtig aufgerufen wird")
    void speicherUrlaub() throws Exception {
        Student student = mock(Student.class);
        OAuth2User user = mock(OAuth2User.class);
        KlausurData klausurData = new KlausurData(LocalDate.of(2022,3,3), LocalTime.of(8,30), LocalTime.of(10,0),true);
        Set<KlausurData> angemeldeteKlausuren = Set.of(klausurData);
        Urlaub urlaub = mock(Urlaub.class);
        Set<Urlaub> gueltigerNeuerUrlaub = Set.of(urlaub);
        when(studentRepo.findByID(anyLong())).thenReturn(student);
        when(klausurRepo.findAngemeldeteKlausuren(anyLong())).thenReturn(angemeldeteKlausuren);
        when(studentService.validiereUrlaub(student,urlaub, angemeldeteKlausuren, BEGINN, ENDE, STARTDATUM, ENDDATUM)). thenReturn(gueltigerNeuerUrlaub);
        when(student.ueberschneidendenUrlaubMergen()).thenReturn(gueltigerNeuerUrlaub);


        service.speicherUrlaub(urlaub, 1234,user );

        verify(studentRepo, times(1)).findByID(anyLong());
        verify(klausurRepo, times(1)).findAngemeldeteKlausuren(anyLong());
        verify(studentService,times(1)).validiereUrlaub(student,urlaub, angemeldeteKlausuren, BEGINN, ENDE, STARTDATUM, ENDDATUM);
        verify(student, times(1)).addUrlaube(gueltigerNeuerUrlaub);
        verify(student, times(1)).ueberschneidendenUrlaubMergen();
        verify(student, times(1)).setUrlaube(gueltigerNeuerUrlaub);
        verify(studentRepo, times(1)).speicherStudent(student);
    }

    @Test
    @DisplayName("Prueft ob urlaubStornieren richtig aufgerufen wird")
    void urlaubStornieren() throws Exception {
        OAuth2User user = mock(OAuth2User.class);
        Student student = mock(Student.class);
        when(studentRepo.findByID(anyLong())).thenReturn(student);
        when(user.getAttribute(anyString())).thenReturn(anyLong());

        service.urlaubStornieren(user, " ", " ", " " );

        verify(studentRepo, times(1)).findByID(anyLong());
        verify(student, times(1)).entferneUrlaub(anyString(),anyString(), anyString());
        verify(studentRepo, times(1)).speicherStudent(student);
    }

    @Test
    @DisplayName("ladeAngemeldeteKlausuren wird korrekt aufgerufen")
    void ladeAngemeldeteKlausuren() {
        KlausurRef klausurRef = mock(KlausurRef.class);
        Klausur klausur = mock(Klausur.class);
        Set<KlausurRef> angemeldeteKlausurenRef = Set.of(klausurRef);
        Set<Klausur> angemeldeteKlausuren = Set.of(klausur);
        when(studentRepo.getAngemeldeteKlausurenIds(anyLong())).thenReturn(angemeldeteKlausurenRef);
        when(klausurRepo.getKlausurenByRefs(angemeldeteKlausurenRef)).thenReturn(angemeldeteKlausuren);
        Map<Klausur, Boolean> stornierbar = new HashMap<>();
        stornierbar.put(klausur, true);
        when(klausurService.stornierbareKlausuren(angemeldeteKlausuren)).thenReturn(stornierbar);

        service.ladeAngemeldeteKlausuren(1234);

        verify(studentRepo, times(1)).getAngemeldeteKlausurenIds(1234);
        verify(klausurRepo, times(1)).getKlausurenByRefs(angemeldeteKlausurenRef);
        verify(klausurService, times(1)).stornierbareKlausuren(angemeldeteKlausuren);
    }

    @Test
    @DisplayName("ladeAngemeldeteUrlaube wird korrekt aufgerufen")
    void ladeAngemeldeteUrlaube() throws Exception {
        Student student = mock(Student.class);
        Urlaub urlaub = mock(Urlaub.class);
        Set<Urlaub> urlaube = Set.of(urlaub);
        Map<Urlaub, Boolean> stornierbar = new HashMap<>();
        stornierbar.put(urlaub, true);
        when(studentRepo.findByID(anyLong())).thenReturn(student);
        when(studentService.stornierbareUrlaube(urlaube)).thenReturn(stornierbar);
        when(student.getUrlaube()).thenReturn(urlaube);

        service.ladeAngemeldeteUrlaube(1234);

        verify(studentRepo, times(1)).findByID(1234);
        verify(studentService, times(1)).stornierbareUrlaube(urlaube);
    }

    @Test
    @DisplayName("speicherStudent wird korrekt aufgerufen")
    void speicherStudent() {
        Student student = mock(Student.class);
        when(studentRepo.existsById(anyLong())).thenReturn(false);
        when(studentRepo.speicherStudent(student)).thenReturn(null);

        service.speicherStudent(student);

        verify(studentRepo, times(1)).existsById(anyLong());
        verify(studentRepo, times(1)).speicherStudent(student);
    }

    @Test
    @DisplayName("findStudentByGithubID wird korrekt aufgerufen")
    void findStudentByGithubID() throws Exception {
        Student student = mock(Student.class);
        when(studentRepo.findByID(anyLong())).thenReturn(student);

        service.findStudentByGithubID(1234);

        verify(studentRepo, times(1)).findByID(1234);
    }
}


