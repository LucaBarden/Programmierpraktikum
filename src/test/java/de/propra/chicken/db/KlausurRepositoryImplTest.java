package de.propra.chicken.db;
import de.propra.chicken.domain.model.Klausur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import static org.junit.jupiter.api.Assertions.*;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@ActiveProfiles("test")
@Sql({"classpath:testCreate.sql"})
public class KlausurRepositoryImplTest {

    CRUDKlausur crud;
    KlausurRepositoryImpl db;

    public KlausurRepositoryImplTest(@Autowired CRUDKlausur crud) {
        this.crud = crud;
        this.db = new KlausurRepositoryImpl(crud);
    }

    @Test
    @DisplayName("Testet, ob eine Klausur korrekt gespeichert wird, wenn noch keine existiert")
    void test1() {
        Klausur klausur = new Klausur("RA", 12335, true, "1999-01-01", "08:30", "09:30");

        db.speicherKlausur(klausur);

        Optional<Klausur> result = crud.findeKlausurByID(klausur.getLsfid()).map(db::transferDTOToKlausur);
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(klausur);
    }

    @Test
    @Sql({"classpath:testData.sql"})
    @DisplayName("Testet, ob eine Klausur richtig abgerufen wird, wenn mehrere existieren")
    void test2() throws Exception {

        Klausur klausur;
        klausur = db.findeKlausurByID(54321);

        assertThat(klausur.getLsfid()).isEqualTo(54321);
    }

    @Test
    @Sql({"classpath:testData.sql"})
    @DisplayName("Testet, ob eine Klausur richtig abgerufen wird, wenn mehrere existieren")
    void test3() throws Exception {

        Klausur klausur;
        klausur = db.findeKlausurByID(54321);

        assertThat(klausur.getLsfid()).isEqualTo(54321);
    }
}
