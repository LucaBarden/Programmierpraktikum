package de.propra.chicken.domain.service;

import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.KlausurRef;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class StudentTests {

    @Test
    @DisplayName("Der Student wirft einen Fehler, wenn er sich bei einer bereits angemeldeten Klausur anmelden möchte")
    public void test1() {
        //arrange
        Student student = new Student(123456);
        Klausur klausur = new Klausur("RA", 44445, false, LocalDate.now().plusDays(2).toString(), LocalTime.of(10,0,0).toString(), LocalTime.of(11,30,0).toString());
        KlausurRef klausurRef = new KlausurRef(klausur.getLsfid());
        StudentService studentService = new StudentService();
        Set<Klausur> angemeldeteKlausuren = Set.of(klausur);
        student.addKlausur(klausurRef);

        Exception thrown =assertThrows(Exception.class,() -> studentService.validiereKlausurAnmeldung(klausur, angemeldeteKlausuren, new HashSet<Urlaub>()));
        assertThat(thrown.getMessage()).isEqualTo("Du bist bereits bei der Klausur angemeldet");
    }

    @Test
    @DisplayName("Der Student wirft keinen Fehler, wenn er sich bei einer neuen Klausur anmelden möchte")
    public void test2() {
        //arrange
        Student student = new Student(123456);
        Klausur klausur = new Klausur("RA", 44445, false, LocalDate.now().plusDays(2).toString(), LocalTime.of(10,0,0).toString(), LocalTime.of(11,30,0).toString());
        Klausur klausur2 = new Klausur("Rechnernetze", 54654, false, LocalDate.now().plusDays(2).toString(), LocalTime.of(10,0,0).toString(), LocalTime.of(11,30,0).toString());
        KlausurRef klausurRef = new KlausurRef(klausur.getLsfid());
        StudentService studentService = new StudentService();
        Set<Klausur> angemeldeteKlausuren = Set.of(klausur);
        student.addKlausur(klausurRef);

        assertDoesNotThrow(() -> studentService.validiereKlausurAnmeldung(klausur2, angemeldeteKlausuren, new HashSet<Urlaub>()));
    }

    @Test
    @DisplayName("Der Student wirft einen Fehler, wenn die Klausur am gleichen Tag ist wie die Anmeldung")
    public void test3() {
        //arrange
        Student student = new Student(123456);
        Klausur klausur = new Klausur("RA", 44445, false, LocalDate.now().toString(), LocalTime.of(10,0,0).toString(), LocalTime.of(11,30,0).toString());
        StudentService studentService = new StudentService();

        Exception thrown = assertThrows(Exception.class,() -> studentService.validiereKlausurAnmeldung(klausur, new HashSet<Klausur>(), new HashSet<Urlaub>() ));
        assertThat(thrown.getMessage()).isEqualTo("Klausur findet heute statt. Anmeldung nicht mehr moeglich");
    }
}
