package de.propra.chicken.db;

import de.propra.chicken.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJdbcTest
@ActiveProfiles("test")
@Sql({"classpath:testCreate.sql"})
public class StudentRepositoryImplTest {

    final CRUDStudent crud;
    final StudentRepositoryImpl db;

    public StudentRepositoryImplTest(@Autowired CRUDStudent crud) {
        this.crud = crud;
        this.db = new StudentRepositoryImpl(crud);
    }

    @Test
    @Sql({"classpath:testCreate.sql", "classpath:testDataKlausur.sql"})
    @DisplayName("Testet, ob ein gefüllter Student richtig gespeichert wird, wenn er noch nicht existiert")
    void test1() {
        //ARRANGE
        Student student = new Student(99999, 240);
        student.setUrlaube(Set.of(new Urlaub("2022-03-04", "08:30", "09:00"), new Urlaub("2022-03-06", "09:30", "10:00")));
        student.setKlausuren(Set.of(new KlausurRef(12345), new KlausurRef(54321)));
        Student studentSaved;
        //ACT
        studentSaved = db.speicherStudent(student);
        //ASSERT
        //überprüft, ob die Rückgabe korrekt ist
        assertThat(studentSaved.getGithubID()).isEqualTo(student.getGithubID());
        assertThat(studentSaved.getResturlaub()).isEqualTo(student.getResturlaub());
        assertThat(studentSaved.getKlausuren()).isEqualTo(student.getKlausuren());
        assertThat(studentSaved.getUrlaube()).isEqualTo(student.getUrlaube());

        //überprüft, ob es in der Datenbank korrekt gespeichert war
        StudentDTO studentDTOByGithubID = crud.findStudentDTOByGithubID(99999).orElse(null);
        Set<KlausurRef> refs = crud.findAngemeldeteKlausurenIds(99999);
        Student studentDB = db.transferDTOToStudent(studentDTOByGithubID, refs);
        assertThat(studentDB.getGithubID()).isEqualTo(student.getGithubID());
        assertThat(studentDB.getResturlaub()).isEqualTo(student.getResturlaub());
        assertThat(studentDB.getUrlaube()).isEqualTo(student.getUrlaube());
        assertThat(studentDB.getKlausuren()).isEqualTo(student.getKlausuren());
    }

    @Test
    @Sql({"classpath:testCreate.sql", "classpath:testDataStudent.sql"})
    @DisplayName("Testet, ob ein gefüllter Student updated wird, wenn er schon existiert")
    void test2() {
        //ARRANGE
        Student student = new Student(99999, 230);
        student.setUrlaube(Set.of(new Urlaub("2022-04-04", "08:00", "09:45"), new Urlaub("2022-03-08", "09:45", "10:30")));
        student.setKlausuren(Set.of(new KlausurRef(54321)));
        Student studentSaved;
        //ACT
        studentSaved = db.speicherStudent(student);
        //ASSERT
        //überprüft, ob die Rückgabe korrekt ist
        assertThat(studentSaved.getGithubID()).isEqualTo(student.getGithubID());
        assertThat(studentSaved.getResturlaub()).isEqualTo(student.getResturlaub());
        assertThat(studentSaved.getKlausuren()).isEqualTo(student.getKlausuren());
        assertThat(studentSaved.getUrlaube()).isEqualTo(student.getUrlaube());
    }

    @Test
    @Sql({"classpath:testCreate.sql","classpath:testDataStudent.sql"})
    @DisplayName("Testet, ob ein Student gefunden wird")
    void test3() {
        //ARRANGE
        Student student = new Student(99999, 240);
        student.setUrlaube(Set.of(new Urlaub("2022-03-04", "08:30", "09:00"), new Urlaub("2022-03-06", "09:30", "10:00")));
        student.setKlausuren(Set.of(new KlausurRef(12345), new KlausurRef(54321)));
        //ACT
        AtomicReference<Student> studentDB = new AtomicReference<>();
        assertDoesNotThrow(() -> studentDB.set(db.findByID(99999)));
        //ASSERT

        //überprüft, ob es in der Datenbank korrekt gespeichert war
        assertThat(studentDB.get().getGithubID()).isEqualTo(student.getGithubID());
        assertThat(studentDB.get().getResturlaub()).isEqualTo(student.getResturlaub());
        assertThat(studentDB.get().getUrlaube()).isEqualTo(student.getUrlaube());
        assertThat(studentDB.get().getKlausuren()).isEqualTo(student.getKlausuren());
    }

    @Test
    @Sql({"classpath:testCreate.sql","classpath:testDataStudent.sql"})
    @DisplayName("Testet, ob ein Fehler geworfen wird, wenn ein Student gesucht wird, der nicht existiert")
    void test4() {
        //ARRANGE
        //ACT //ASSERT
        Exception thrown = assertThrows(Exception.class, () -> db.findByID(35987));
        assertThat(thrown.getMessage()).isEqualTo("Dieser Student existiert nicht");

    }

    @Test
    @Sql({"classpath:testCreate.sql","classpath:testDataStudent.sql"})
    @DisplayName("Testet, ob alle korrekten Klausurreferenzen gefunden werden")
    void test5() {
        //ARRANGE
        Set<KlausurRef> refs = Set.of(new KlausurRef(12345), new KlausurRef(54321));
        //ACT
        Set<KlausurRef> refsDB = db.getAngemeldeteKlausurenIds(99999);
        //ASSERT
        assertThat(refsDB).isEqualTo(refs);
    }

    @Test
    @Sql({"classpath:testCreate.sql","classpath:testDataStudent.sql"})
    @DisplayName("Testet, ob korrekt geprüft wird, ob ein Student existiert, wenn er existiert")
    void test6() {
        //ARRANGE
        //ACT //ASSERT
        assertThat(db.existsById(99999)).isEqualTo(true);
    }

    @Test
    @Sql({"classpath:testCreate.sql","classpath:testDataStudent.sql"})
    @DisplayName("Testet, ob korrekt geprüft wird, ob ein Student existiert, wenn er nicht existiert")
    void test7() {
        //ARRANGE
        //ACT //ASSERT
        assertThat(db.existsById(65424)).isEqualTo(false);
    }
}
