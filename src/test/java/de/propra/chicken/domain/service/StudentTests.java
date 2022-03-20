package de.propra.chicken.domain.service;

import de.propra.chicken.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class StudentTests {

    private static final String BEGINN_PRAKTIKUM = "08:30";
    private static final String ENDE_PRAKTIKUM   = "12:30";

    @Test
    @DisplayName("Der Student wirft einen Fehler, wenn er sich bei einer bereits angemeldeten Klausur anmelden möchte")
    public void test1() {
        //arrange
        Student student = new Student(123456);
        KlausurData klausur = new KlausurData(LocalDate.now().plusDays(2), LocalTime.of(10,0,0), LocalTime.of(11,30,0), false);
        KlausurRef klausurRef = new KlausurRef(44445);
        StudentService studentService = new StudentService();
        Set<KlausurData> angemeldeteKlausuren = Set.of(klausur);
        Set<KlausurRef> angemeldeteKlausurRefs = Set.of(klausurRef);
        student.addKlausur(klausurRef);

        Exception thrown =assertThrows(Exception.class,() -> studentService.validiereKlausurAnmeldung(klausurRef, klausur, angemeldeteKlausuren, student, angemeldeteKlausurRefs));
        assertThat(thrown.getMessage()).isEqualTo("Du bist bereits bei der Klausur angemeldet");
    }

    @Test
    @DisplayName("Der Student wirft keinen Fehler, wenn er sich bei einer neuen Klausur anmelden möchte")
    public void test2() {
        //arrange
        Student student = new Student(123456);
        KlausurData klausur = new KlausurData( LocalDate.now().plusDays(2), LocalTime.of(10,0,0), LocalTime.of(11,30,0),false);
        KlausurData klausur2 = new KlausurData( LocalDate.now().plusDays(2), LocalTime.of(10,0,0), LocalTime.of(11,30,0), false);
        KlausurRef klausurRef = new KlausurRef(44445);
        KlausurRef klausurRef2 = new KlausurRef(54654);

        StudentService studentService = new StudentService();
        Set<KlausurData> angemeldeteKlausuren = Set.of(klausur);
        Set<KlausurRef> angemeldeteKlausurenRef = Set.of(klausurRef);
        student.addKlausur(klausurRef);

        assertDoesNotThrow(() -> studentService.validiereKlausurAnmeldung(klausurRef2, klausur2, angemeldeteKlausuren, student,angemeldeteKlausurenRef));
    }

    @Test
    @DisplayName("Der Student wirft einen Fehler, wenn die Klausur am gleichen Tag ist wie die Anmeldung")
    public void test3() {
        //arrange
        Student student = new Student(123456);
        Klausur klausur = new Klausur("RA", 44445, false, LocalDate.now().toString(), LocalTime.of(10,0,0).toString(), LocalTime.of(11,30,0).toString());
        KlausurRef klausurRef = klausur.getKlausurRef();
        KlausurData klausurData = klausur.getKlausurData();
        StudentService studentService = new StudentService();

        Exception thrown = assertThrows(Exception.class,() -> studentService.validiereKlausurAnmeldung(klausurRef, klausurData, new HashSet<>(), student, new HashSet<>() ));
        assertThat(thrown.getMessage()).isEqualTo("Klausur findet heute statt. Anmeldung nicht mehr moeglich");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: den ganzen Tag Urlaub nehmen")
    void urlaubDenGanzenTag() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub("1999-01-01", "08:30", "12:30");
        Set<KlausurData> klausuren = new HashSet<>();

        assertDoesNotThrow(() -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
    }

    @Test
    @DisplayName("Urlaubsvalidierung: Urlaub liegt vor dem Praktikumszeitraum")
    void urlaubZuFrueh() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub("1999-01-01", "08:00", "09:00");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Der Urlaub muss im Praktikumszeitraum liegen");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: Start ist nach End")
    void urlaubStartAfterEnd() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub("1999-01-01", "10:00", "09:00");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Die Startzeit kann nicht nach der Endzeit liegen");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: Start- und Endzeit gleich")
    void urlaubStartEqualsEnd() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub("1999-01-01", "10:00", "10:00");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Die Startzeit und Endzeit sind gleich!!");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: kein Vielfaches von 15")
    void urlaubKeineViertelstunde() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub("1999-01-01", "10:53", "12:00");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Die Start- und Endzeit muss ein Vielfaches von 15 Minuten sein");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: zu wenig Resturlaub")
    void urlaubResturlaub() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        student.setResturlaub(15);
        Urlaub urlaub = new Urlaub("1999-01-01", "10:00", "10:30");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Es ist zu wenig Resturlaub übrig");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: keine Klausur und kein Urlaub am selben Tag: zu lang")
    void urlaubZuLang() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub("1999-01-01", "08:30", "11:30");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Der Urlaub ist weder den ganzen Tag lang noch weniger als 2.5 Stunden lang");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: keine Klausur am selben Tag: es gibt schon zwei Urlaubsblöcke an diesem Tag")
    void urlaubZuVielUrlaub() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub("1999-01-01", "08:30", "16:00");
        Urlaub urlaub2 = new Urlaub("1999-01-01", "17:00", "18:00");
        Set<Urlaub> urlaube = new HashSet<>();
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        student.setUrlaube(urlaube);
        Urlaub urlaub = new Urlaub("1999-01-01", "08:30", "11:30");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Es wurden bereits zwei Urlaubsblöcke genommen");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: keine Klausur am selben Tag: zwei gültige Blöcke")
    void urlaubZweiGueltigeBloecke() {
        //TODO
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub("1999-01-01", "08:30", "10:00");
        Set<Urlaub> urlaube = new HashSet<>();
        urlaube.add(urlaub1);
        student.setUrlaube(urlaube);
        Urlaub urlaub = new Urlaub("1999-01-01", "12:00", "12:30");
        Set<KlausurData> klausuren = new HashSet<>();

        assertDoesNotThrow(() -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
    }

    @Test
    @DisplayName("Urlaubsvalidierung: keine Klausur am selben Tag: es werden keine 90min gearbeitet zwischen den Blöcken")
    void urlaub90MinArbeiten() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub("1999-01-01", "08:30", "10:00");
        Set<Urlaub> urlaube = new HashSet<>();
        urlaube.add(urlaub1);
        student.setUrlaube(urlaube);
        Urlaub urlaub = new Urlaub("1999-01-01", "11:00", "12:30");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Man muss mindestens 90 Minuten zwischen den beiden Urlaubsblöcken arbeiten");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: keine Klausur am selben Tag: kein Block liegt am Anfang des Praktikums")
    void urlaubBereitsGebuchtNichtAmAnfang() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub("1999-01-01", "08:45", "10:00");
        Set<Urlaub> urlaube = new HashSet<>();
        urlaube.add(urlaub1);
        student.setUrlaube(urlaube);
        Urlaub urlaub = new Urlaub("1999-01-01", "12:00", "12:30");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Der bereits gebuchte Urlaub ist weder am Anfang noch am Ende des Tages");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: keine Klausur am selben Tag: kein Block liegt am Anfang des Praktikums")
    void urlaubNeuNichtAmEnde() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub("1999-01-01", "08:30", "10:00");
        Set<Urlaub> urlaube = new HashSet<>();
        urlaube.add(urlaub1);
        student.setUrlaube(urlaube);
        Urlaub urlaub = new Urlaub("1999-01-01", "12:00", "12:15");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Der neue Urlaub muss am Ende des Tages liegen");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: eine Präsenzklausur am selben Tag, vorher 15 min Urlaub nehmen")
    void urlaubPraesenzklausur() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);

        Urlaub urlaub = new Urlaub("1999-01-01", "08:30", "09:00");

        Set<KlausurData> klausuren = new HashSet<>();
        KlausurData klausurData = new KlausurData(LocalDate.parse("1999-01-01"), LocalTime.parse("10:45"), LocalTime.parse("12:00"), true);
        klausuren.add(klausurData);

        Set<Urlaub> gueltigerUrlaub = new HashSet<>();

        try { //wenn eine Klausur am selben Tag ist werden keine Fehler geworfen, sondern der gültige Urlaub zurückgegeben
            gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(gueltigerUrlaub).hasSize(1);
        assertThat(gueltigerUrlaub.stream().toList().get(0).getBeginn().toString()). isEqualTo("08:30");
        assertThat(gueltigerUrlaub.stream().toList().get(0).getEnd().toString()). isEqualTo("08:45");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: eine Onlineklausur am selben Tag, den ganzen Tag Urlaub nehmen: zwei Urlaubsblöcke werden erstellt")
    void urlaubOnlineklausurGanzenTag() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);

        Urlaub urlaub = new Urlaub("1999-01-01", "08:30", "12:30");

        Set<KlausurData> klausuren = new HashSet<>();
        KlausurData klausurData = new KlausurData( LocalDate.parse("1999-01-01"), LocalTime.parse("10:00"), LocalTime.parse("11:00"), false);
        klausuren.add(klausurData);

        Set<Urlaub> gueltigerUrlaub = new HashSet<>();

        try { //wenn eine Klausur am selben Tag ist werden keine Fehler geworfen, sondern der gültige Urlaub zurückgegeben
            gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Urlaub urlaub1 = new Urlaub("1999-01-01", "08:30", "09:30");
        Urlaub urlaub2 = new Urlaub("1999-01-01", "11:30", "12:30");

        assertThat(gueltigerUrlaub).hasSize(2);
        assertThat(gueltigerUrlaub).contains(urlaub1, urlaub2);
    }

    @Test
    @DisplayName("Urlaubsvalidierung: eine Onlineklausur am selben Tag, nachher Urlaub nehmen")
    void urlaubOnlineklausurSpaeter() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);

        Urlaub urlaub = new Urlaub("1999-01-01", "11:00", "12:30");

        Set<KlausurData> klausuren = new HashSet<>();
        KlausurData klausurData = new KlausurData(LocalDate.parse("1999-01-01"), LocalTime.parse("10:00"), LocalTime.parse("11:00"), false);
        klausuren.add(klausurData);

        Set<Urlaub> gueltigerUrlaub = new HashSet<>();

        try { //wenn eine Klausur am selben Tag ist werden keine Fehler geworfen, sondern der gültige Urlaub zurückgegeben
            gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Urlaub urlaub1 = new Urlaub("1999-01-01", "11:30", "12:30");

        assertThat(gueltigerUrlaub).hasSize(1);
        assertThat(gueltigerUrlaub).contains(urlaub1);
    }

    @Test
    @DisplayName("Urlaubsvalidierung: zwei Onlineklausuren am selben Tag, dazwischen Urlaub nehmen")
    void urlaubZweiOnlineKlausuren() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);

        Urlaub urlaub = new Urlaub("1999-01-01", "09:30", "12:00");

        Set<KlausurData> klausuren = new HashSet<>();
        KlausurData klausurData1 = new KlausurData(LocalDate.parse("1999-01-01"), LocalTime.parse("09:00"), LocalTime.parse("09:30"), false);
        KlausurData klausurData2 = new KlausurData(LocalDate.parse("1999-01-01"), LocalTime.parse("12:00"), LocalTime.parse("12:30"), false);
        klausuren.add(klausurData1);
        klausuren.add(klausurData2);

        Set<Urlaub> gueltigerUrlaub = new HashSet<>();

        try { //wenn eine Klausur am selben Tag ist werden keine Fehler geworfen, sondern der gültige Urlaub zurückgegeben
            gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Urlaub urlaub1 = new Urlaub("1999-01-01", "10:00", "11:30");

        assertThat(gueltigerUrlaub).hasSize(1);
        assertThat(gueltigerUrlaub).contains(urlaub1);
    }

    @Test
    @DisplayName("Urlaubsvalidierung: zwei Onlineklausuren am selben Tag, den ganzen Tag Urlaub nehmen")
    void urlaubZweiOnlineKlausurenUrlaubSplit() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);

        Urlaub urlaub = new Urlaub("1999-01-01", "08:30", "12:30");

        Set<KlausurData> klausuren = new HashSet<>();
        KlausurData klausurData1 = new KlausurData(LocalDate.parse("1999-01-01"), LocalTime.parse("09:15"), LocalTime.parse("09:30"), false);
        KlausurData klausurData2 = new KlausurData(LocalDate.parse("1999-01-01"), LocalTime.parse("11:00"), LocalTime.parse("11:30"), false);
        klausuren.add(klausurData1);
        klausuren.add(klausurData2);

        Set<Urlaub> gueltigerUrlaub = new HashSet<>();

        try { //wenn eine Klausur am selben Tag ist werden keine Fehler geworfen, sondern der gültige Urlaub zurückgegeben
            gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Urlaub urlaub1 = new Urlaub("1999-01-01", "08:30", "08:45");
        Urlaub urlaub2 = new Urlaub("1999-01-01", "10:00", "10:30");
        Urlaub urlaub3 = new Urlaub("1999-01-01", "12:00", "12:30");

        System.out.println(gueltigerUrlaub.size());
        System.out.println(gueltigerUrlaub);
        assertThat(gueltigerUrlaub.size()).isEqualTo(3);
        assertThat(gueltigerUrlaub).contains(urlaub1, urlaub2, urlaub3);
    }

    @Test
    @DisplayName("Urlaubsvalidierung: zwei Onlineklausuren am selben Tag, den ganzen Tag Urlaub nehmen")
    void urlaubZweiVerschiedeneKlausurenUrlaubSplit() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);

        Urlaub urlaub = new Urlaub("1999-01-01", "08:30", "12:30");

        Set<KlausurData> klausuren = new HashSet<>();
        KlausurData klausurData1 = new KlausurData(LocalDate.parse("1999-01-01"), LocalTime.parse("09:15"), LocalTime.parse("09:30"), true);
        KlausurData klausurData2 = new KlausurData(LocalDate.parse("1999-01-01"), LocalTime.parse("11:00"), LocalTime.parse("11:30"), false);
        klausuren.add(klausurData1);
        klausuren.add(klausurData2);

        Set<Urlaub> gueltigerUrlaub = new HashSet<>();

        try { //wenn eine Klausur am selben Tag ist werden keine Fehler geworfen, sondern der gültige Urlaub zurückgegeben
            gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Urlaub urlaub1 = new Urlaub("1999-01-01", "12:00", "12:30");

        System.out.println(gueltigerUrlaub.size());
        System.out.println(gueltigerUrlaub);
        assertThat(gueltigerUrlaub.size()).isEqualTo(1);
        assertThat(gueltigerUrlaub).contains(urlaub1);
    }

}
