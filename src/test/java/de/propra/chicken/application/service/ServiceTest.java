package de.propra.chicken.application.service;

import de.propra.chicken.application.service.repo.KlausurRepository;
import de.propra.chicken.application.service.repo.StudentRepository;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.KlausurData;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;
import de.propra.chicken.domain.service.KlausurService;
import de.propra.chicken.domain.service.StudentService;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServiceTest {

    private static final Dotenv dotenv = Dotenv.load();

    private static String BEGINN     = dotenv.get("STARTUHRZEIT_PRAKTIKUM");
    private static String ENDE       = dotenv.get("ENDUHRZEIT_PRAKTIKUM");
    private static String STARTDATUM = dotenv.get("STARTDATUM_PRAKTIKUM");
    private static String ENDDATUM   = dotenv.get("ENDDATUM_PRAKTIKUM");

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
    @DisplayName("Klausuranmeldung wird korrekt aufgerufen")
    public void klausurAnmeldungTest() throws Exception {
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
    public void saveKlausur() throws Exception {
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
    public void ladeAlleKlausuren() {
        Klausur klausur = mock(Klausur.class);
        Set<Klausur> klausur1 = Set.of(klausur);
        when(klausurRepo.ladeAlleKlausuren()).thenReturn(klausur1);

        service.ladeAlleKlausuren();

        verify(klausurRepo, times(1)).ladeAlleKlausuren();
        verify(klausurService, times(1)).klausurIstNochImAnmeldezeitraum(klausur1);

    }

    @Test
    @DisplayName("Prueft ob die Klausur Stornierung richtig aufgerufen wird")
    public void klausurStornieren() throws Exception {
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
    public void speicherUrlaub() throws Exception {
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
    public void urlaubStornieren() throws Exception {
        OAuth2User user = mock(OAuth2User.class);
        Student student = mock(Student.class);
        when(studentRepo.findByID(anyLong())).thenReturn(student);
        when(user.getAttribute(anyString())).thenReturn(anyLong());

        service.urlaubStornieren(user, " ", " ", " " );

        verify(studentRepo, times(1)).findByID(anyLong());
        verify(student, times(1)).entferneUrlaub(anyString(),anyString(), anyString());
        verify(studentRepo, times(1)).speicherStudent(student);


    }


    


}


