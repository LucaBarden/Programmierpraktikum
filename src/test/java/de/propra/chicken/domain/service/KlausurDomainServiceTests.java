package de.propra.chicken.domain.service;

import de.propra.chicken.domain.model.Klausur;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class KlausurDomainServiceTests {


    List<Klausur> arrange(){
        List<Klausur> alleKlausuren = new LinkedList<>();
        //gueltig
        alleKlausuren.add(new Klausur("Rechnerarchitektur", 12, true, LocalDate.now().plusDays(1), null, null));
        alleKlausuren.add(new Klausur("Progra", 34, false, LocalDate.now().plusDays(3), null, null));
        //ungueltig
        alleKlausuren.add(new Klausur("RDB", 56, false, LocalDate.now(), null, null));
        alleKlausuren.add(new Klausur("AlDat", 78, false, LocalDate.now().minusDays(2), null, null));

        return alleKlausuren;

    }

    @Test
    @DisplayName("Testet, dass nur Klausuren in der Zukunft geladen werden")
    void klausurenUngueltig() {
        //arrange
        List<Klausur> alleKlausuren = arrange();
        //act
        List<Klausur> gueltigeKlausuren = KlausurDomainService.validiereAlleKlausuren(alleKlausuren);

        //assert
        assertThat(gueltigeKlausuren).hasSize(2);
        assertThat(gueltigeKlausuren.get(0).getLsfid()).isEqualTo(12);
        assertThat(gueltigeKlausuren.get(1).getLsfid()).isEqualTo(34);
    }


    @Test
    @DisplayName("Testet ob nur gültige Klausuren auf true gesetzt werden")
    void klausurenStornierbar() {
        //arrange
        List<Klausur> alleKlausuren = arrange();
        //act
        Map<Klausur, Boolean> stornierbareKlausuren = KlausurDomainService.stornierbareKlausuren(alleKlausuren);

        assertThat(stornierbareKlausuren).hasSize(4);
        assertThat(stornierbareKlausuren.get(alleKlausuren.get(0))).isEqualTo(true);
        assertThat(stornierbareKlausuren.get(alleKlausuren.get(1))).isEqualTo(true);
        assertThat(stornierbareKlausuren.get(alleKlausuren.get(2))).isEqualTo(false);
        assertThat(stornierbareKlausuren.get(alleKlausuren.get(3))).isEqualTo(false);

    }






}
