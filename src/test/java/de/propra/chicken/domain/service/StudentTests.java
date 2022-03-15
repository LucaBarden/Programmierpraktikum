package de.propra.chicken.domain.service;

import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;

public class StudentTests {

    @Test
    @DisplayName("Der Student wirft einen Fehler, wenn er sich bei einer bereits angemeldeten Klausur anmelden möchte")
    public void test1() {
        //arrange
        Student student = new Student(123456);
        Klausur klausur = new Klausur("RA", 44445, false, LocalDate.now().plusDays(2), LocalTime.of(10,0,0), LocalTime.of(11,30,0));
        student.addKlausur(klausur);

        Exception thrown =assertThrows(Exception.class,() -> { student.validiereKlausurAnmeldung(klausur);});
        assertThat(thrown.getMessage()).isEqualTo("Du bist bereits bei der Klausur angemeldet");
    }

    @Test
    @DisplayName("Der Student wirft keinen Fehler, wenn er sich bei einer neuen Klausur anmelden möchte")
    public void test2() {
        //arrange
        Student student = new Student(123456);
        Klausur klausur = new Klausur("RA", 44445, false, LocalDate.now().plusDays(2), LocalTime.of(10,0,0), LocalTime.of(11,30,0));
        Klausur klausur2 = new Klausur("Rechnernetze", 54654, false, LocalDate.now().plusDays(2), LocalTime.of(10,0,0), LocalTime.of(11,30,0));
        student.addKlausur(klausur);

        assertDoesNotThrow(() -> { student.validiereKlausurAnmeldung(klausur2);});
    }

    @Test
    @DisplayName("Der Student wirft einen Fehler, wenn die Klausur am gleichen Tag ist")
    public void test3() {
        //arrange
        Student student = new Student(123456);
        Klausur klausur = new Klausur("RA", 44445, false, LocalDate.now(), LocalTime.of(10,0,0), LocalTime.of(11,30,0));

        Exception thrown = assertThrows(Exception.class,() -> { student.validiereKlausurAnmeldung(klausur);});
        assertThat(thrown.getMessage()).isEqualTo("Klausur findet heute statt. Anmeldung nicht mehr moeglich");
    }
}
