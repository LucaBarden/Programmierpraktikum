package de.propra.chicken.db;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.KlausurData;
import de.propra.chicken.domain.model.KlausurRef;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import static org.junit.jupiter.api.Assertions.*;


import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@ActiveProfiles("test")
@Sql({"classpath:testCreate.sql"})
public class KlausurRepositoryImplTest {

    final CRUDKlausur crud;
    final KlausurRepositoryImpl db;

    public KlausurRepositoryImplTest(@Autowired CRUDKlausur crud) {
        this.crud = crud;
        this.db = new KlausurRepositoryImpl(crud);
    }

    @Test
    @Sql({"classpath:testCreate.sql"})
    @DisplayName("Testet, ob eine Klausur korrekt gespeichert wird, wenn noch keine existiert")
    void test1() {
        //ARRANGE
        Klausur klausur = new Klausur("RA", 12335, true, "1999-01-01", "08:30", "09:30");
        AtomicReference<Klausur> klausurSaved = new AtomicReference<>();
        //ACT
        assertDoesNotThrow(() -> klausurSaved.set(db.speicherKlausur(klausur)));
        //ASSERT
        assertThat(klausurSaved.get()).isEqualTo(klausur);
        Optional<Klausur> result = crud.findeKlausurByID(klausur.getLsfid()).map(db::transferDTOToKlausur);
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(klausur);
    }

    @Test
    @Sql({"classpath:testCreate.sql", "classpath:testDataKlausur.sql"})
    @DisplayName("Testet, ob eine Klausur nicht gespeichert wird und keine Änderung vornimmt, wenn die Klausur schon existiert")
    void test2() {
        //ARRANGE
        Klausur klausur = new Klausur("Aldat", 54321, false, "2022-05-04", "07:00", "09:00");
        //ACT
        Exception thrown = assertThrows(Exception.class, () -> db.speicherKlausur(klausur));
        //ASSERT
        assertThat(thrown.getMessage()).isEqualTo("Eine Klausur mit dieser ID existiert bereits");
        Optional<Klausur> saved = crud.findeKlausurByID(klausur.getLsfid()).map(db::transferDTOToKlausur);
        assertThat(saved).isNotEmpty();
        assertThat(saved.get().getKlausurData()).isNotEqualTo(klausur.getKlausurData());
        assertThat(saved.get().getName()).isNotEqualTo(klausur.getName());
    }

    @Test
    @Sql({"classpath:testCreate.sql", "classpath:testDataKlausur.sql"})
    @DisplayName("Testet, ob eine Klausur richtig abgerufen wird, wenn mehrere existieren")
    void test3() {
        //ARRANGE
        AtomicReference<Klausur> klausur = new AtomicReference<>();
        //ACT
        assertDoesNotThrow(() -> klausur.set(db.findeKlausurByID(54321)));
        //ASSERT
        assertThat(klausur.get().getLsfid()).isEqualTo(54321);
    }

    @Test
    @Sql({"classpath:testCreate.sql", "classpath:testDataKlausur.sql"})
    @DisplayName("Testet, dass ein Fehler geworfen wird wenn eine Klausur gesucht wird, die nicht existiert")
    void test4() {
        //ARRANGE
        //ACT //ASSERT
        Exception thrown = assertThrows(Exception.class, () -> db.findeKlausurByID(65498));
        assertThat(thrown.getMessage()).isEqualTo("Diese Klausur Existiert nicht");
    }

    @Test
    @Sql({"classpath:testCreate.sql", "classpath:testDataKlausur.sql"})
    @DisplayName("Testet, dass alle Klausuren gefunden werden")
    void test5() {
        //ARRANGE
        Klausur klausur1 = new Klausur("RA", 12345, true, "1999-01-01", "08:30", "12:30");
        Klausur klausur2 = new Klausur("RDB", 54321, true, "1999-03-04", "09:00", "11:30");
        Set<Klausur> klausuren = Set.of(klausur1, klausur2);
        //ACT
        Set<Klausur> klausurenDB = db.ladeAlleKlausuren();
        //ASSERT
        assertThat(klausurenDB).isEqualTo(klausuren);
    }

    @Test
    @Sql({"classpath:testCreate.sql", "classpath:testDataKlausur.sql", "classpath:testDataKlausur2.sql"})
    @DisplayName("Testet, dass nur bestimmte Klausuren mit KlausurRef gefunden werden")
    void test6() {
        //ARRANGE
        Klausur klausur1 = new Klausur("RDB", 54321, true, "1999-03-04", "09:00", "11:30");
        Klausur klausur2 = new Klausur("ProPra", 66666, true, "1999-01-02", "06:00", "12:30");
        Set<Klausur> klausuren = Set.of(klausur1, klausur2);
        Set<KlausurRef> refs = Set.of(new KlausurRef(54321), new KlausurRef(66666));
        //ACT
        Set<Klausur> klausurenDB = db.getKlausurenByRefs(refs);
        Set<KlausurRef> refsDB = klausurenDB.stream().map(Klausur::getKlausurRef).collect(Collectors.toSet());
        //ASSERT
        assertThat(klausuren).isEqualTo(klausurenDB);
    }

    @Test
    @Sql({"classpath:testCreate.sql", "classpath:testDataKlausur.sql", "classpath:testDataKlausur2.sql"})
    @DisplayName("Testet, dass nur bestimmte Klausuren mit KlausurRef gefunden und zu KlausurData konvertiert werden")
    void test7() {
        //ARRANGE
        Klausur klausur1 = new Klausur("RDB", 54321, true, "1999-03-04", "09:00", "11:30");
        Klausur klausur2 = new Klausur("ProPra", 66666, true, "1999-01-02", "06:00", "12:30");
        Set<KlausurData> data = Set.of(klausur1.getKlausurData(), klausur2.getKlausurData());
        Set<KlausurRef> refs = Set.of(new KlausurRef(54321), new KlausurRef(66666));
        //ACT
        Set<KlausurData> klausurenDB = db.getKlausurenDataByRefs(refs);
        //ASSERT
        assertThat(klausurenDB).isEqualTo(data);
    }

    @Test
    @Sql({"classpath:testCreate.sql", "classpath:testDataStudent.sql", "classpath:testDataKlausur2.sql"})
    @DisplayName("Testet, dass nur bestimmte Klausuren mit KlausurRef gefunden und zu KlausurData konvertiert werden")
    void test8() {
        //ARRANGE
        Klausur klausur1 = new Klausur("RA", 12345, true, "1999-01-01", "08:30", "12:30");
        Klausur klausur2 = new Klausur("RDB", 54321, true, "1999-03-04", "09:00", "11:30");
        Set<KlausurData> data = Set.of(klausur1.getKlausurData(), klausur2.getKlausurData());
        Set<KlausurRef> refs = Set.of(new KlausurRef(54321), new KlausurRef(12345));
        //ACT
        Set<KlausurData> klausurenDB = db.findAngemeldeteKlausuren(99999);
        //ASSERT
        assertThat(klausurenDB).isEqualTo(data);
    }








}
