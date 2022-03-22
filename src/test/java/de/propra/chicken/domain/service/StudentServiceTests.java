package de.propra.chicken.domain.service;

import de.propra.chicken.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StudentServiceTests {

    private static final String BEGINN_PRAKTIKUM = "08:30";
    private static final String ENDE_PRAKTIKUM   = "12:30";

    @Test
    @DisplayName("Der Student wirft einen Fehler, wenn er sich bei einer bereits angemeldeten Klausur anmelden möchte")
    public void test1() {
        LocalDate tag = LocalDate.now().plusDays(2);
        Student student = new Student(123456);
        Klausur klausur = new Klausur("RA", 44445, false, tag.toString(), LocalTime.of(10,0,0).toString(), LocalTime.of(11,30,0).toString());
        KlausurRef klausurRef = new KlausurRef(44445);
        StudentService studentService = new StudentService();
        student.addKlausur(klausurRef);

        Exception thrown = assertThrows(Exception.class,() -> studentService.validiereKlausurAnmeldung(klausur, student, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Du bist bereits bei der Klausur angemeldet");
    }

    @Test
    @DisplayName("Der Student wirft keinen Fehler, wenn er sich bei einer neuen Klausur anmelden möchte")
    public void test2() {
        LocalDate tag = LocalDate.now().plusDays(2);
        Student student = new Student(123456);
        //KlausurData klausur = new KlausurData(tag, LocalTime.of(10,0,0), LocalTime.of(11,30,0),false);
        Klausur klausur2 = new Klausur("RA", 54654, false, tag.toString(), LocalTime.of(10,0,0).toString(), LocalTime.of(11,30,0).toString());
        KlausurRef klausurRef = new KlausurRef(44445);
        //KlausurRef klausurRef2 = new KlausurRef(54654);
        StudentService studentService = new StudentService();
        //Set<KlausurData> angemeldeteKlausuren = Set.of(klausur);
        //Set<KlausurRef> angemeldeteKlausurenRef = Set.of(klausurRef);
        student.addKlausur(klausurRef);

        assertDoesNotThrow(() -> studentService.validiereKlausurAnmeldung(klausur2, student, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
    }

    @Test
    @DisplayName("Der Student wirft einen Fehler, wenn die Klausur am gleichen Tag ist wie die Anmeldung")
    public void test3() {
        Student student = new Student(123456);
        Klausur klausur = new Klausur("RA", 44445, false, LocalDate.now().toString(), LocalTime.of(10,0,0).toString(), LocalTime.of(11,30,0).toString());
        //KlausurRef klausurRef = klausur.getKlausurRef();
        //KlausurData klausurData = klausur.getKlausurData();
        StudentService studentService = new StudentService();

        Exception thrown = assertThrows(Exception.class,() -> studentService.validiereKlausurAnmeldung(klausur, student, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM ));
        assertThat(thrown.getMessage()).isEqualTo("Klausur findet heute statt. Anmeldung nicht mehr moeglich");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: den ganzen Tag Urlaub nehmen ist gültig")
    void urlaubsvalidierung1() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub(LocalDate.now().plusDays(2).toString(), "08:30", "12:30");
        Set<KlausurData> klausuren = new HashSet<>();

        assertDoesNotThrow(() -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
    }

    @Test
    @DisplayName("Urlaubsvalidierung: Urlaub liegt vor dem Praktikumszeitraum: Exception wird geworfen")
    void urlaubsvalidierung2() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub("1000-01-01", "08:00", "09:00");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Der Urlaub muss im Praktikumszeitraum liegen");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: Start des Urlaubs ist nach End: Exception wird geworfen")
    void urlaubsvalidierung3() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub(LocalDate.now().plusDays(1).toString(), "10:00", "09:00");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Die Startzeit kann nicht nach der Endzeit liegen");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: Start- und Endzeit des Urlaubs gleich: Exception wird geworfen")
    void urlaubsvalidierung4() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub(LocalDate.now().plusDays(2).toString(), "10:00", "10:00");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Die Startzeit und Endzeit sind gleich!!");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: Urlaubszeit ist kein Vielfaches von 15 Minuten: Exception wird geworfen")
    void urlaubsvalidierung5() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub(LocalDate.now().plusDays(2).toString(), "10:53", "12:00");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Die Start- und Endzeit muss ein Vielfaches von 15 Minuten sein");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: zu wenig Resturlaub um diesen Urlaub zu buchen: Exception wird geworfen")
    void urlaubsvalidierung6() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        student.setResturlaub(15);
        Urlaub urlaub = new Urlaub(LocalDate.now().plusDays(2).toString(), "10:00", "10:30");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Es ist zu wenig Resturlaub übrig");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: der Urlaub ist weder den ganzen Tag lang noch weniger als 2.5h: Exception werfen")
    void urlaubsvalidierung7() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub(LocalDate.now().plusDays(2).toString(), "08:30", "11:30");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Der Urlaub ist weder den ganzen Tag lang noch weniger als 2.5 Stunden lang");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: der zu buchende Urlaub ist heute: zu spät um den Urlaub zu buchen")
    void urlaubsvalidierung8() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub(LocalDate.now().toString(), "08:30", "11:30");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Man kann Urlaub spätestens einen Tag vorher buchen.");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: es gibt schon zwei Urlaubsblöcke an diesem Tag: Exception werfen")
    void urlaubsvalidierung9() {
        String tag = LocalDate.now().plusDays(2).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub(tag, "08:30", "16:00");
        Urlaub urlaub2 = new Urlaub(tag, "17:00", "18:00");
        Set<Urlaub> urlaube = new HashSet<>();
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        student.setUrlaube(urlaube);
        Urlaub urlaub = new Urlaub(tag, "08:30", "11:30");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Es wurden bereits zwei Urlaubsblöcke genommen");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: zwei gültige Blöcke: keine Exception wird geworfen")
    void urlaubsvalidierung10() {
        String tag = LocalDate.now().plusDays(2).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub(tag, "08:30", "10:00");
        Set<Urlaub> urlaube = new HashSet<>();
        urlaube.add(urlaub1);
        student.setUrlaube(urlaube);
        Urlaub urlaub = new Urlaub(tag, "12:00", "12:30");
        Set<KlausurData> klausuren = new HashSet<>();

        assertDoesNotThrow(() -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
    }

    @Test
    @DisplayName("Urlaubsvalidierung: es werden keine 90min gearbeitet zwischen den Urlaubsblöcken: Exception wird geworfen")
    void urlaubsvalidierung11() {
        String tag = LocalDate.now().plusDays(2).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub(tag, "08:30", "10:00");
        Set<Urlaub> urlaube = new HashSet<>();
        urlaube.add(urlaub1);
        student.setUrlaube(urlaube);
        Urlaub urlaub = new Urlaub(tag, "11:00", "12:30");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Man muss mindestens 90 Minuten zwischen den beiden Urlaubsblöcken arbeiten");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: die beiden Urlaubsblöcke liegen nicht am Anfang und am Ende: Exception wird geworfen")
    void urlaubsvalidierung12() {
        String tag = LocalDate.now().plusDays(2).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub(tag, "08:45", "10:00");
        Set<Urlaub> urlaube = new HashSet<>();
        urlaube.add(urlaub1);
        student.setUrlaube(urlaube);
        Urlaub urlaub = new Urlaub(tag, "12:00", "12:30");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Der bereits gebuchte Urlaub ist weder am Anfang noch am Ende des Tages");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: die beiden Urlaubsblöcke liegen nicht am Anfang und am Ende: Exception wird geworfen")
    void urlaubsvalidierung13() {
        String tag = LocalDate.now().plusDays(2).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub(tag, "08:30", "10:00");
        Set<Urlaub> urlaube = new HashSet<>();
        urlaube.add(urlaub1);
        student.setUrlaube(urlaube);
        Urlaub urlaub = new Urlaub(tag, "12:00", "12:15");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Der neue Urlaub muss am Ende des Tages liegen");
    }

    @Test
    @DisplayName("Urlaubsvalidierung: eine Präsenzklausur am selben Tag, vorher 30 min Urlaub nehmen; 15 min davon werden erstattet")
    void urlaubsvalidierung14() {
        String tag = LocalDate.now().plusDays(2).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(123);

        Urlaub urlaub = new Urlaub(tag, "08:30", "09:00");
        Set<KlausurData> klausuren = new HashSet<>();
        KlausurData klausurData = new KlausurData(LocalDate.parse(tag), LocalTime.parse("10:45"), LocalTime.parse("12:00"), true);
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
    @DisplayName("Urlaubsvalidierung: eine Onlineklausur am selben Tag, den ganzen Tag Urlaub nehmen: zwei Urlaubsblöcke werden erstellt, dazwischen liegt die Klausur und der Urlaub wird erstattet")
    void urlaubsvalidierung15() {
        String tag = LocalDate.now().plusDays(2).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(123);

        Urlaub urlaub = new Urlaub(tag, "08:30", "12:30");
        Set<KlausurData> klausuren = new HashSet<>();
        KlausurData klausurData = new KlausurData(LocalDate.parse(tag), LocalTime.parse("10:00"), LocalTime.parse("11:00"), false);
        klausuren.add(klausurData);
        Set<Urlaub> gueltigerUrlaub = new HashSet<>();

        try { //wenn eine Klausur am selben Tag ist werden keine Fehler geworfen, sondern der gültige Urlaub zurückgegeben
            gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Urlaub urlaub1 = new Urlaub(LocalDate.now().plusDays(2).toString(), "08:30", "09:30");
        Urlaub urlaub2 = new Urlaub(LocalDate.now().plusDays(2).toString(), "11:00", "12:30");

        assertThat(gueltigerUrlaub).hasSize(2);
        assertThat(gueltigerUrlaub).contains(urlaub1, urlaub2);
    }

    @Test
    @DisplayName("Urlaubsvalidierung: eine Onlineklausur am selben Tag, nachher Urlaub nehmen; Anfang des Urlaubs wird aufgrund von einer Überschneidung mit der Klausur erstattet")
    void urlaubsvalidierung16() {
        String tag = LocalDate.now().plusDays(1).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(123);

        Urlaub urlaub = new Urlaub(tag, "10:30", "12:30");
        Set<KlausurData> klausuren = new HashSet<>();
        KlausurData klausurData = new KlausurData(LocalDate.parse(tag), LocalTime.parse("10:00"), LocalTime.parse("11:00"), false);
        klausuren.add(klausurData);
        Set<Urlaub> gueltigerUrlaub = new HashSet<>();

        try { //wenn eine Klausur am selben Tag ist werden keine Fehler geworfen, sondern der gültige Urlaub zurückgegeben
            gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Urlaub urlaub1 = new Urlaub(tag, "11:00", "12:30");

        assertThat(gueltigerUrlaub).hasSize(1);
        assertThat(gueltigerUrlaub).contains(urlaub1);
    }

    @Test
    @DisplayName("Urlaubsvalidierung: zwei Onlineklausuren am selben Tag, dazwischen Urlaub nehmen; der Urlaub wird vorne und hinten gekürzt aufgrund von Überschneidung mit der Klausur")
    void urlaubsvalidierung17() {
        String tag = LocalDate.now().plusDays(1).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(123);

        Urlaub urlaub = new Urlaub(tag, "09:15", "12:00");
        Set<KlausurData> klausuren = new HashSet<>();
        KlausurData klausurData1 = new KlausurData(LocalDate.parse(tag), LocalTime.parse("09:00"), LocalTime.parse("09:30"), false);
        KlausurData klausurData2 = new KlausurData(LocalDate.parse(tag), LocalTime.parse("12:00"), LocalTime.parse("12:30"), false);
        klausuren.add(klausurData1);
        klausuren.add(klausurData2);
        Set<Urlaub> gueltigerUrlaub = new HashSet<>();

        try { //wenn eine Klausur am selben Tag ist werden keine Fehler geworfen, sondern der gültige Urlaub zurückgegeben
            gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Urlaub urlaub1 = new Urlaub(tag, "09:30", "11:30");

        assertThat(gueltigerUrlaub).hasSize(1);
        assertThat(gueltigerUrlaub).contains(urlaub1);
    }

    @Test
    @DisplayName("Urlaubsvalidierung: zwei Onlineklausuren am selben Tag, den ganzen Tag Urlaub nehmen; der Urlaub wird in drei Blöcke aufgeteilt")
    void urlaubsvalidierung18() {
        String tag = LocalDate.now().plusDays(1).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(123);

        Urlaub urlaub = new Urlaub(tag, "08:30", "12:30");
        Set<KlausurData> klausuren = new HashSet<>();
        KlausurData klausurData1 = new KlausurData(LocalDate.parse(tag), LocalTime.parse("09:15"), LocalTime.parse("09:30"), false);
        KlausurData klausurData2 = new KlausurData(LocalDate.parse(tag), LocalTime.parse("11:00"), LocalTime.parse("11:30"), false);
        klausuren.add(klausurData1);
        klausuren.add(klausurData2);
        Set<Urlaub> gueltigerUrlaub = new HashSet<>();

        try { //wenn eine Klausur am selben Tag ist werden keine Fehler geworfen, sondern der gültige Urlaub zurückgegeben
            gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Urlaub urlaub1 = new Urlaub(tag, "08:30", "08:45");
        Urlaub urlaub2 = new Urlaub(tag, "09:30", "10:30");
        Urlaub urlaub3 = new Urlaub(tag, "11:30", "12:30");

        System.out.println(gueltigerUrlaub.size());
        System.out.println(gueltigerUrlaub);
        assertThat(gueltigerUrlaub.size()).isEqualTo(3);
        assertThat(gueltigerUrlaub).contains(urlaub1, urlaub2, urlaub3);
    }

    @Test
    @DisplayName("Urlaubsvalidierung: zwei Onlineklausuren am selben Tag, den ganzen Tag Urlaub nehmen, der Urlaub wird vorne und hinten gekürzt aufgrund von Überschneidungen mit den Klausuren")
    void urlaubsvalidierung19() {
        String tag = LocalDate.now().plusDays(1).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(123);

        Urlaub urlaub = new Urlaub(tag, "08:30", "12:30");
        Set<KlausurData> klausuren = new HashSet<>();
        KlausurData klausurData1 = new KlausurData(LocalDate.parse(tag), LocalTime.parse("09:15"), LocalTime.parse("09:30"), true);
        KlausurData klausurData2 = new KlausurData(LocalDate.parse(tag), LocalTime.parse("11:00"), LocalTime.parse("11:30"), false);
        klausuren.add(klausurData1);
        klausuren.add(klausurData2);
        Set<Urlaub> gueltigerUrlaub = new HashSet<>();

        try { //wenn eine Klausur am selben Tag ist werden keine Fehler geworfen, sondern der gültige Urlaub zurückgegeben
            gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Urlaub urlaub1 = new Urlaub(tag, "11:30", "12:30");

        System.out.println(gueltigerUrlaub.size());
        System.out.println(gueltigerUrlaub);
        assertThat(gueltigerUrlaub.size()).isEqualTo(1);
        assertThat(gueltigerUrlaub).contains(urlaub1);
    }

    @Test
    @DisplayName("Urlaubsvalidierung: eine Onlineklausur und einen Urlaub am selben Tag")
    void urlaubsvalidierung20() {
        String tag = LocalDate.now().plusDays(1).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(123);

        Urlaub urlaub = new Urlaub(tag, "08:30", "09:30");
        Urlaub neuerUrlaub = new Urlaub(tag, "09:15", "10:00");
        Set<Urlaub> urlaube = new HashSet<>();
        urlaube.add(urlaub);
        student.addUrlaube(urlaube);
        Set<KlausurData> klausuren = new HashSet<>();
        KlausurData klausurData1 = new KlausurData(LocalDate.parse(tag), LocalTime.parse("10:00"), LocalTime.parse("11:00"), false);
        klausuren.add(klausurData1);
        Set<Urlaub> gueltigerUrlaub = new HashSet<>();

        try { //wenn eine Klausur am selben Tag ist werden keine Fehler geworfen, sondern der gültige Urlaub zurückgegeben
            gueltigerUrlaub = studentService.validiereUrlaub(student, neuerUrlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(gueltigerUrlaub.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("ganzer Tag Urlaub, der sich mit neu zu anmeldender Klausur überschneidet, also wird Teil des Urlaubs erstattet")
    void klasusurAnmeldungTest1() {
        String tag = LocalDate.now().plusDays(1).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(234);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub(tag, "08:30", "12:30");
        urlaube.add(urlaub1);
        student.setUrlaube(urlaube);
        //KlausurRef lsfID = new KlausurRef(12345);
        Klausur klausur = new Klausur("RA", 12345, false, tag, "08:30", "10:00");
        Urlaub uebrigerUrlaub = new Urlaub(tag, "10:00", "12:30");
        Set<Urlaub> gueltigerUrlaub = new HashSet<>();

        try{
             gueltigerUrlaub = studentService.validiereKlausurAnmeldung(klausur, student,  BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
                e.printStackTrace();
        }

        assertThat(gueltigerUrlaub).hasSize(1);
        assertThat(gueltigerUrlaub).contains(uebrigerUrlaub);
    }

    @Test
    @DisplayName("ganzer Tag Urlaub, der sich mit neu zu anmeldender Praesenz-Klausur überschneidet, also wird alles erstattet")
    void klasusurAnmeldungTest2() {
        String tag = LocalDate.now().plusDays(1).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(234);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub(tag, "08:30", "12:30");
        urlaube.add(urlaub1);
        student.setUrlaube(urlaube);
        //KlausurRef lsfID = new KlausurRef(12345);
        Klausur klausur = new Klausur("RA", 12345, true, tag, "09:00", "10:30");
        Set<Urlaub> gueltigerUrlaub = new HashSet<>();

        try{
            gueltigerUrlaub = studentService.validiereKlausurAnmeldung(klausur, student,  BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(gueltigerUrlaub).hasSize(0);
    }

    @Test
    @DisplayName("bereits angemeldeter Urlaub wird gelöscht, nachdem man sich für eine Praesenz-Klausur angemeldet hat, die sich mit Urlaub überschneidet")
    void klausurAnmeldungTest3() {
        String tag = LocalDate.now().plusDays(1).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(234);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub(tag, "08:30", "10:30");
        urlaube.add(urlaub1);
        student.setUrlaube(urlaube);
        Klausur klausur= new Klausur("RA", 12345, true, tag, "08:30", "10:00");


        Set<Urlaub> gueltigerUrlaub = new HashSet<>();
        try{
            gueltigerUrlaub = studentService.validiereKlausurAnmeldung(klausur, student,BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(gueltigerUrlaub).hasSize(0);

    }
    @Test
    @DisplayName("Anfang eines Urlaubs wird erstattet, nachdem man sich für eine Praesenz-Klausur angemeldet hat, die sich mit Urlaub überschneidet")
    void klausurAnmeldungTest4() {
        String tag = LocalDate.now().plusDays(1).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(234);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub(tag, "11:30", "12:30");
        urlaube.add(urlaub1);
        student.setUrlaube(urlaube);
        Klausur klausur= new Klausur("RA", 12345, true, tag, "08:30", "10:00");
        Urlaub uebrigerUrlaub = new Urlaub(tag, "12:00", "12:30");

        Set<Urlaub> gueltigerUrlaub = new HashSet<>();
        try{
            gueltigerUrlaub = studentService.validiereKlausurAnmeldung(klausur, student,BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(gueltigerUrlaub).hasSize(1);
        assertThat(gueltigerUrlaub).contains(uebrigerUrlaub);
    }
    @Test
    @DisplayName("Ende eines Urlaubs wird erstattet, nachdem man sich für eine Praesenz-Klausur angemeldet hat, die sich mit Urlaub überschneidet")
    void klausurAnmeldungTest5() {
        String tag = LocalDate.now().plusDays(1).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(234);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub(tag, "08:30", "10:30");
        urlaube.add(urlaub1);
        student.setUrlaube(urlaube);
        Klausur klausur= new Klausur("RA", 12345, true, tag, "12:00", "12:30");
        Urlaub uebrigerUrlaub = new Urlaub(tag, "08:30", "10:00");

        Set<Urlaub> gueltigerUrlaub = new HashSet<>();
        try{
            gueltigerUrlaub = studentService.validiereKlausurAnmeldung(klausur, student,BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(gueltigerUrlaub).hasSize(1);
        assertThat(gueltigerUrlaub).contains(uebrigerUrlaub);
    }
    @Test
    @DisplayName("Mitte eines Urlaubs wird erstattet, nachdem man sich für eine Online-Klausur angemeldet hat, die sich mit Urlaub überschneidet")
    void klausurAnmeldungTest6() {
        String tag = LocalDate.now().plusDays(1).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(234);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub(tag, "08:30", "11:00");
        urlaube.add(urlaub1);
        student.setUrlaube(urlaube);
        Klausur klausur= new Klausur("RA", 12345, false, tag, "09:30", "10:30");
        Urlaub uebrigerUrlaub1 = new Urlaub(tag, "08:30", "09:00");
        Urlaub uebrigerUrlaub2 = new Urlaub(tag, "10:30", "11:00");

        Set<Urlaub> gueltigerUrlaub = new HashSet<>();
        try{
            gueltigerUrlaub = studentService.validiereKlausurAnmeldung(klausur, student,BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(gueltigerUrlaub).hasSize(2);
        assertThat(gueltigerUrlaub).contains(uebrigerUrlaub1, uebrigerUrlaub2);
    }




    @Test
    @DisplayName("bereits fuer zwei Urlaube angemeldet, nachdem man sich fuer eine Klausur angemeldet hat, werden beide gekuerzt")
    void klausurAnmeldungTest7() throws Exception {
        String tag = LocalDate.now().plusDays(1).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(234);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub(tag, "08:30", "09:30");
        urlaube.add(urlaub1);
        Urlaub urlaub2 = new Urlaub(tag, "10:30", "12:30");
        urlaube.add(urlaub2);
        student.setUrlaube(urlaube);
        Klausur klausur= new Klausur("RA",12345, false, tag, "09:30", "11:00");
        Urlaub result = new Urlaub(tag, "08:30", "09:00");
        Urlaub result2 = new Urlaub(tag, "11:00", "12:30");

        Set<Urlaub> gueltigerUrlaub = new HashSet<>();
        try{
            gueltigerUrlaub = studentService.validiereKlausurAnmeldung(klausur, student,BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(gueltigerUrlaub).hasSize(2);
        assertThat(gueltigerUrlaub).contains(result, result2);
    }

    @Test
    @DisplayName("bereits für zwei Urlaube angemeldet,  nachdem man sich für eine Klausur angemeldet hat, wird einer gelöscht und der andere gekürzt")
    void klausurAnmeldungTest8() throws Exception {
        String tag = LocalDate.now().plusDays(1).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(234);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub(tag, "08:30", "09:30");
        urlaube.add(urlaub1);
        Urlaub urlaub2 = new Urlaub(tag, "11:00", "12:30");
        urlaube.add(urlaub2);
        student.setUrlaube(urlaube);
        Klausur klausur = new Klausur("RA",12345,true,tag, "11:00", "12:00");
        Urlaub result = new Urlaub(tag, "08:30", "09:00");

        Set<Urlaub> gueltigerUrlaub = new HashSet<>();
        try{
            gueltigerUrlaub = studentService.validiereKlausurAnmeldung(klausur, student,BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(gueltigerUrlaub).hasSize(1);
        assertThat(gueltigerUrlaub).contains(result);
    }

    @Test
    @DisplayName("bereits für zwei Urlaube angemeldet,  nachdem man sich für eine Klausur angemeldet hat, werden beide gelöscht")
    void klausurAnmeldungTest9() throws Exception {
        String tag = LocalDate.now().plusDays(1).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(234);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub(tag, "08:30", "09:30");
        urlaube.add(urlaub1);
        Urlaub urlaub2 = new Urlaub(tag, "11:00", "12:30");
        urlaube.add(urlaub2);
        student.setUrlaube(urlaube);
        Klausur klausur = new Klausur("RA",12345, true,tag,"10:30", "12:00");

        Set<Urlaub> gueltigerUrlaub = new HashSet<>();
        try{
            gueltigerUrlaub = studentService.validiereKlausurAnmeldung(klausur, student,BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(gueltigerUrlaub).hasSize(0);
    }

    @Test
    @DisplayName("bereits für zwei Urlaube und eine Klausur angemeldet,  nachdem man sich für eine Klausur angemeldet hat, wird Urlaub gelöscht und einer gekürzt")
    void klausurAnmeldungTest10() throws Exception {
        String tag = LocalDate.now().plusDays(1).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(234);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub(tag, "08:30", "09:00");
        urlaube.add(urlaub1);
        Urlaub urlaub2 = new Urlaub(tag, "11:00", "12:30");
        urlaube.add(urlaub2);
        student.setUrlaube(urlaube);
        Klausur klausur = new Klausur("RA", 12345,true,tag, "11:00", "12:00");
        Urlaub result = new Urlaub(tag, "08:30", "09:00");

        Set<Urlaub> gueltigerUrlaub = new HashSet<>();
        try{
            gueltigerUrlaub = studentService.validiereKlausurAnmeldung(klausur, student,BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(gueltigerUrlaub).hasSize(1);
        assertThat(gueltigerUrlaub).contains(result);
    }

    @Test
    @DisplayName("stornierbareUrlaube")
    void stornierbareUrlaube() {
        String heute = LocalDate.now().toString();
        String gestern = LocalDate.now().minusDays(1).toString();
        String morgen = LocalDate.now().plusDays(1).toString();
        String naechsteWoche = LocalDate.now().plusDays(7).toString();
        StudentService studentService = new StudentService();
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub(heute, "08:30", "10:00"); //nicht stornierbar
        Urlaub urlaub2 = new Urlaub(morgen, "11:30", "12:00"); //stornierbar
        Urlaub urlaub3 = new Urlaub(gestern, "09:00", "10:30"); //nicht stornierbar
        Urlaub urlaub4 = new Urlaub(naechsteWoche, "08:30", "12:30"); //stornierbar
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        urlaube.add(urlaub3);
        urlaube.add(urlaub4);

        Map<Urlaub, Boolean> stornierbar = studentService.stornierbareUrlaube(urlaube);

        assertThat(stornierbar).hasSize(4);
        assertThat(stornierbar.get(urlaub1)).isEqualTo(false);
        assertThat(stornierbar.get(urlaub2)).isEqualTo(true);
        assertThat(stornierbar.get(urlaub3)).isEqualTo(false);
        assertThat(stornierbar.get(urlaub4)).isEqualTo(true);
    }

}