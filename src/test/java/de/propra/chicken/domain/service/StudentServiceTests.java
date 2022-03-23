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
    private static final String STARTDATUM = LocalDate.now().minusDays(7).toString();
    private static final String ENDDATUM   = LocalDate.now().plusDays(7).toString();

    @Test
    @DisplayName("Fehler, wenn Student sich bei einer bereits angemeldeten Klausur anmelden möchte")
    public void test1() {
        LocalDate tag = LocalDate.now().plusDays(2);
        Student student = new Student(123456);
        Klausur klausur = new Klausur("RA", 44445, false, tag.toString(),
                LocalTime.of(10,0,0).toString(), LocalTime.of(11,30,0).toString());
        KlausurRef klausurRef = new KlausurRef(44445);
        StudentService studentService = new StudentService();
        student.addKlausur(klausurRef);

        Exception thrown = assertThrows(Exception.class,() -> studentService.validiereKlausurAnmeldung(klausur, student,
                BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
        assertThat(thrown.getMessage()).isEqualTo("Du bist bereits bei der Klausur angemeldet");
    }

    @Test
    @DisplayName("Kein Fehler, wenn Student sich bei einer neuen Klausur anmelden möchte")
    public void test2() {
        LocalDate tag = LocalDate.now().plusDays(2);
        Student student = new Student(123456);
        //KlausurData klausur = new KlausurData(tag, LocalTime.of(10,0,0), LocalTime.of(11,30,0),false);
        Klausur klausur2 = new Klausur("RA", 54654, false, tag.toString(),
                LocalTime.of(10,0,0).toString(), LocalTime.of(11,30,0).toString());
        KlausurRef klausurRef = new KlausurRef(44445);
        //KlausurRef klausurRef2 = new KlausurRef(54654);
        StudentService studentService = new StudentService();
        //Set<KlausurData> angemeldeteKlausuren = Set.of(klausur);
        //Set<KlausurRef> angemeldeteKlausurenRef = Set.of(klausurRef);
        student.addKlausur(klausurRef);

        assertDoesNotThrow(() -> studentService.validiereKlausurAnmeldung(klausur2, student, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
    }

    @Test
    @DisplayName("Fehler, wenn die Klausur am gleichen Tag ist wie die Anmeldung")
    public void test3() {
        Student student = new Student(123456);
        Klausur klausur = new Klausur("RA", 44445, false, LocalDate.now().toString(),
                LocalTime.of(10,0,0).toString(), LocalTime.of(11,30,0).toString());
        //KlausurRef klausurRef = klausur.getKlausurRef();
        //KlausurData klausurData = klausur.getKlausurData();
        StudentService studentService = new StudentService();

        Exception thrown = assertThrows(Exception.class,() -> studentService.validiereKlausurAnmeldung(klausur, student,
                BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM ));
        assertThat(thrown.getMessage()).isEqualTo("Klausur findet heute statt. Anmeldung nicht mehr moeglich");
    }

    @Test
    @DisplayName("Kein Fehler, wenn ganzer Tag Urlaub genommen wird")
    void urlaubsvalidierung1() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub(LocalDate.now().plusDays(2).toString(), "08:30", "12:30");
        Set<KlausurData> klausuren = new HashSet<>();

        assertDoesNotThrow(() -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM,
                ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));
    }

    @Test
    @DisplayName("Fehler, wenn Urlaubsstart vor dem Praktikumszeitraum liegt")
    void urlaubsvalidierung2() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub(LocalDate.now().plusDays(1).toString(), "08:00", "09:00");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren,
                BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));
        assertThat(thrown.getMessage()).isEqualTo("Der Urlaub muss im Praktikumszeitraum liegen (Uhrzeit)");
    }

    @Test
    @DisplayName("Fehler, wenn Startzeit nach Endzeit des Urlaubs liegt")
    void urlaubsvalidierung3() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub(LocalDate.now().plusDays(1).toString(), "10:00", "09:00");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren,
                BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));
        assertThat(thrown.getMessage()).isEqualTo("Die Startzeit kann nicht nach der Endzeit liegen");
    }

    @Test
    @DisplayName("Fehler, wenn Urlaubsdatum vor dem Praktikumszeitraum liegt")
    void urlaubsvalidierung21() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub(LocalDate.now().minusDays(9).toString(), "08:30", "09:00");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren,
                BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));
        assertThat(thrown.getMessage()).isEqualTo("Das Urlaubsdatum liegt vor dem Praktikumsbeginn!");
    }

    @Test
    @DisplayName("Fehler, wenn Urlaubsdatum nach Praktikumszeitraum liegt")
    void urlaubsvalidierung22() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub(LocalDate.now().plusDays(9).toString(), "08:30", "09:00");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren,
                BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));
        assertThat(thrown.getMessage()).isEqualTo("Das Urlaubsdatum liegt nach dem Praktikumsende!");
    }

    @Test
    @DisplayName("Fehler, wenn Start- und Endzeit des Urlaubs gleich sind")
    void urlaubsvalidierung4() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub(LocalDate.now().plusDays(2).toString(), "10:00", "10:00");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren,
                BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));
        assertThat(thrown.getMessage()).isEqualTo("Die Startzeit und Endzeit sind gleich!!");
    }

    @Test
    @DisplayName("Fehler,wenn Urlaubszeit ist kein Vielfaches von 15 Minuten ist")
    void urlaubsvalidierung5() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub(LocalDate.now().plusDays(2).toString(), "10:53", "12:00");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren,
                BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));
        assertThat(thrown.getMessage()).isEqualTo("Die Start- und Endzeit muss ein Vielfaches von 15 Minuten sein");
    }

    @Test
    @DisplayName("Fehler, wenn zu wenig Resturlaub da ist um diesen Urlaub zu buchen")
    void urlaubsvalidierung6() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        student.setResturlaub(15);
        Urlaub urlaub = new Urlaub(LocalDate.now().plusDays(2).toString(), "10:00", "10:30");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren,
                BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));
        assertThat(thrown.getMessage()).isEqualTo("Es ist zu wenig Resturlaub übrig");
    }

    @Test
    @DisplayName("Fehler, wenn der Urlaub weder den ganzen Tag lang noch weniger als 2.5h lang ist")
    void urlaubsvalidierung7() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub(LocalDate.now().plusDays(2).toString(), "08:30", "11:30");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren,
                BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));
        assertThat(thrown.getMessage()).isEqualTo("Der Urlaub ist weder den ganzen Tag lang noch weniger als 2.5 Stunden lang");
    }

    @Test
    @DisplayName("Fehler, wenn der zu buchende Urlaub am gleichen Tag wie die Buchung ist, also es zu spaet ist um den Urlaub zu buchen")
    void urlaubsvalidierung8() {
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub = new Urlaub(LocalDate.now().toString(), "08:30", "11:30");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren,
                BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));
        assertThat(thrown.getMessage()).isEqualTo("Man kann Urlaub spätestens einen Tag vorher buchen.");
    }

    @Test
    @DisplayName("Fehler, wenn bereits zwei Urlaubsbloecke gebucht wurden und ein dritter hinzugefuegt werden soll")
    void urlaubsvalidierung9() {
        String tag = LocalDate.now().plusDays(2).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub(tag, "08:30", "09:00");
        Urlaub urlaub2 = new Urlaub(tag, "10:00", "11:00");
        Set<Urlaub> urlaube = new HashSet<>();
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        student.setUrlaube(urlaube);
        Urlaub urlaub = new Urlaub(tag, "11:30", "12:00");
        Set<KlausurData> klausuren = new HashSet<>();

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren,
                BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));
        assertThat(thrown.getMessage()).isEqualTo("Es wurden bereits zwei Urlaubsblöcke genommen");
    }

    @Test
    @DisplayName("Kein Fehler, wenn ein Urlaub am Anfang des Tages bereits existiert" +
            " und ein weiterer am Ende des Tages hinzugefuegt werden soll")
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

        assertDoesNotThrow(() -> studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM,
                ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));
    }

    @Test
    @DisplayName("Fehler, wenn keine 90 min zwischen dem bereits gebuchten und zu buchendem Urlaub gearbeitet wird")
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

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren,
                BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));
        assertThat(thrown.getMessage()).isEqualTo("Man muss mindestens 90 Minuten zwischen den beiden Urlaubsblöcken arbeiten");
    }

    @Test
    @DisplayName("Fehler, wenn der bereits gebuchte Urlaub nicht am Anfang des Tages liegt, der zu buchende Urlaub " +
            "am Ende des Tages, also die Urlaubsbloecke nicht am Anfang und Ende des Tages liegen")
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

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren,
                BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));
        assertThat(thrown.getMessage()).isEqualTo("Der bereits gebuchte Urlaub ist weder am Anfang noch am Ende des Tages");
    }

    @Test
    @DisplayName("Fehler, wenn der bereits gebuchte Urlaub am Anfang des Tages liegt, aber der zu buchende Urlaub " +
            "nicht am Ende des Tages, also die Urlaubsbloecke nicht am Anfang und Ende des Tages liegen")
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

        Exception thrown = assertThrows(Exception.class, () -> studentService.validiereUrlaub(student, urlaub, klausuren,
                BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));
        assertThat(thrown.getMessage()).isEqualTo("Der neue Urlaub muss am Ende des Tages liegen");
    }

    @Test
    @DisplayName("Richtige Erstattung des Urlaubs bei einer Praesenzklausur am selben Tag, die sich 15 min lang mit dem Urlaub ueberschneidet")
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
            gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM,
                    ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(gueltigerUrlaub).hasSize(1);
        assertThat(gueltigerUrlaub.stream().toList().get(0).getBeginn().toString()). isEqualTo("08:30");
        assertThat(gueltigerUrlaub.stream().toList().get(0).getEnd().toString()). isEqualTo("08:45");
    }

    @Test
    @DisplayName("Richtige Erstattung des Urlaubs bei einer Onlineklausur, wenn man sich den ganzen Tag Urlaub nehmen will:" +
            " zwei Urlaubsbloecke werden erstellt, der Zeitraum der Klausur wird erstattet")
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
            gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM,
                    ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Urlaub urlaub1 = new Urlaub(LocalDate.now().plusDays(2).toString(), "08:30", "09:30");
        Urlaub urlaub2 = new Urlaub(LocalDate.now().plusDays(2).toString(), "11:00", "12:30");

        assertThat(gueltigerUrlaub).hasSize(2);
        assertThat(gueltigerUrlaub).contains(urlaub1, urlaub2);
    }

    @Test
    @DisplayName("Richtige Erstattung des Urlaubs bei einer Onlineklausur am selben Tag," +
            " wenn sich der Anfang des Urlaubs mit der Klausur ueberschneidet")
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
            gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM,
                    STARTDATUM, ENDDATUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Urlaub urlaub1 = new Urlaub(tag, "11:00", "12:30");

        assertThat(gueltigerUrlaub).hasSize(1);
        assertThat(gueltigerUrlaub).contains(urlaub1);
    }

    @Test
    @DisplayName("Richtige Erstattung des Urlaubs bei zwei Onlineklausuren am selben Tag," +
            "wenn der Urlaub sich am Anfang und am Ende mit den Klausuren ueberschneidet")
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
            gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM,
                    STARTDATUM, ENDDATUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Urlaub urlaub1 = new Urlaub(tag, "09:30", "11:30");

        assertThat(gueltigerUrlaub).hasSize(1);
        assertThat(gueltigerUrlaub).contains(urlaub1);
    }

    @Test
    @DisplayName("Richtige Erstattung des Urlaubs bei zwei Onlineklausuren am selben Tag," +
            "wenn der Urlaub fuer einen ganzen Tag mit den Klausuren  der Urlaub wird in drei Blöcke aufgeteilt")
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
            gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM,
                    STARTDATUM, ENDDATUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Urlaub urlaub1 = new Urlaub(tag, "08:30", "08:45");
        Urlaub urlaub2 = new Urlaub(tag, "09:30", "10:30");
        Urlaub urlaub3 = new Urlaub(tag, "11:30", "12:30");

        assertThat(gueltigerUrlaub.size()).isEqualTo(3);
        assertThat(gueltigerUrlaub).contains(urlaub1, urlaub2, urlaub3);
    }

    @Test
    @DisplayName("Richtige Erstattung des Urlaubs bei zwei Onlineklausuren am selben Tag," +
            "wenn der Urlaub sich am Anfang und am Ende jeweils mit einer Klausur ueberschneidet")
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
            gueltigerUrlaub = studentService.validiereUrlaub(student, urlaub, klausuren, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM,
                    STARTDATUM, ENDDATUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Urlaub urlaub1 = new Urlaub(tag, "11:30", "12:30");

        assertThat(gueltigerUrlaub.size()).isEqualTo(1);
        assertThat(gueltigerUrlaub).contains(urlaub1);
    }

    @Test
    @DisplayName("Richtige Erstattung des gesamten Urlaubs wegen kompletter Ueberschneidung mit einer Onlineklausur")
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
            gueltigerUrlaub = studentService.validiereUrlaub(student, neuerUrlaub, klausuren, BEGINN_PRAKTIKUM,
                    ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(gueltigerUrlaub.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Bei Online-Klausuranmeldung wird der Anfang des bereits gebuchten Urlaubs richtig erstattet")
    void klasusurAnmeldungTest1() {
        String tag = LocalDate.now().plusDays(1).toString();
        StudentService studentService = new StudentService();
        Student student = new Student(234);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub(tag, "08:30", "12:30");
        urlaube.add(urlaub1);
        student.setUrlaube(urlaube);
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
    @DisplayName("Bei Praesenz-Klausuranmeldung wird der ganze Tag Urlaub richtig erstattet")
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
    @DisplayName("Bei Praesenz-Klausuranmeldung wird gesamter Urlaub richtig erstattet")
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
    @DisplayName("Bei Praesenz-Klausuranmeldung wird der Anfang eines Urlaubs richtig erstattet, der sich mit Klausur ueberschneidet")
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
    @DisplayName("Bei Praesenz-Klausuranmeldung wird das Ende eines Urlaubs richtig erstattet, der sich mit Klausur ueberschneidet")
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
    @DisplayName("Bei Online-Klausur wird die Mitte eines Urlaubs richtig erstattet, der sich mit der Klausur ueberschneidet")
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
    @DisplayName("Bei Online-Klausuranmeldung wird ein Urlaub am Ende, der andere am Anfang richtig erstattet aufgrund der Ueberschneidung")
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
    @DisplayName("Bei Praesenz-Klausuranmeldung wird ein Urlaub komplett, der andere am Ende richtig erstattet aufgrund der Ueberschneidung ")
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
    @DisplayName("Bei Praesenz-Klausuranmeldung werden beide komplett richtig erstattet aufgrund der Ueberschneidung")
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
    @DisplayName("Bei Praesenz-Klausuranmeldung wird ein Urlaub komplett, der andere am Anfang richtig erstattet")
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
        Klausur klausur = new Klausur("RA", 12345,true,tag, "08:30", "10:00");
        Urlaub result = new Urlaub(tag, "12:00", "12:30");

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
    @DisplayName("Die Urlaube, die in der Zukunft liegen, sind stornierbar(also true), die heute oder vorher waren sind nicht stornierbar")
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