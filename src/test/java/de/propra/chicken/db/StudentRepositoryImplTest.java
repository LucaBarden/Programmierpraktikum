package de.propra.chicken.db;

import de.propra.chicken.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@ActiveProfiles("test")
@Sql({"classpath:testCreate.sql"})
public class StudentRepositoryImplTest {

    CRUDStudent crud;
    StudentRepositoryImpl db;

    public StudentRepositoryImplTest(@Autowired CRUDStudent crud) {
        this.crud = crud;
        this.db = new StudentRepositoryImpl(crud);
    }

    @Test
    @Sql({"classpath:testCreate.sql", "classpath:testDataKlausur.sql"})
    @DisplayName("Testet, ob ein gefüllter richtig Student gespeichert wird")
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
        Set<KlausurRef> refs = crud.findAngemeldeteKlausurenIds(66666);
        Student studentDB = db.transferDTOToStudent(studentDTOByGithubID, refs);
        assertThat(studentDB.getGithubID()).isEqualTo(student.getGithubID());
        assertThat(studentDB.getResturlaub()).isEqualTo(student.getResturlaub());
        assertThat(studentDB.getUrlaube()).isEqualTo(student.getUrlaube());
        assertThat(studentDB.getKlausuren()).isEqualTo(student.getKlausuren());
    }

    @Test
    @Sql({"classpath:testCreate.sql", "classpath:testDataKlausur.sql"})
    @DisplayName("Testet, ob ein gefüllter richtig Student gespeichert wird")
    void test2() {
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
        Set<KlausurRef> refs = crud.findAngemeldeteKlausurenIds(66666);
        Student studentDB = db.transferDTOToStudent(studentDTOByGithubID, refs);
        assertThat(studentDB.getGithubID()).isEqualTo(student.getGithubID());
        assertThat(studentDB.getResturlaub()).isEqualTo(student.getResturlaub());
        assertThat(studentDB.getUrlaube()).isEqualTo(student.getUrlaube());
        assertThat(studentDB.getKlausuren()).isEqualTo(student.getKlausuren());
    }
}
