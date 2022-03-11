package de.propra.chicken.domain.service;

import de.propra.chicken.domain.model.Klausur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class KlausurDomainServiceTests {

    @Test
    @DisplayName("Testet, dass nur Klausuren in der Zukunft zurückgegeben werden")
    void klausurUngueltig() {
        //arrange
        List<Klausur> alleKlausuren = new LinkedList<>();
        //gueltig
        alleKlausuren.add(new Klausur("Rechnerarchitektur", 12, true, LocalDate.now().plusDays(1), null, null));
        alleKlausuren.add(new Klausur("Progra", 34, false, LocalDate.now().plusDays(3), null, null));
        //ungueltig
        alleKlausuren.add(new Klausur("RDB", 56, false, LocalDate.now(), null, null));
        alleKlausuren.add(new Klausur("AlDat", 78, false, LocalDate.now().minusDays(2), null, null));

        //act
        List<Klausur> gueltigeKlausuren = KlausurDomainService.validiereAlleKlausuren(alleKlausuren);

        //assert
        assertThat(gueltigeKlausuren).hasSize(2);
        assertThat(gueltigeKlausuren.get(0).getLsfid()).isEqualTo(12);
        assertThat(gueltigeKlausuren.get(1).getLsfid()).isEqualTo(34);
    }

    //stornierbareKlausuren(List<Klausur> klausuren)




}
