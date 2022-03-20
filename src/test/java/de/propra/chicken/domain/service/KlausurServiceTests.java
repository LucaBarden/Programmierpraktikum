package de.propra.chicken.domain.service;

import de.propra.chicken.domain.model.Klausur;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class KlausurServiceTests {

    private static final String BEGINN_PRAKTIKUM = "08:30";
    private static final String ENDE_PRAKTIKUM   = "12:30";

    Set<Klausur> arrange(){
        Set<Klausur> alleKlausuren = new HashSet<>();
        //gueltig
        alleKlausuren.add(new Klausur("Rechnerarchitektur", 12, true, LocalDate.now().plusDays(1).toString(), "10:00", "10:00"));
        alleKlausuren.add(new Klausur("Progra", 34, false, LocalDate.now().plusDays(3).toString(), "10:00", "10:00"));
        //ungueltig
        alleKlausuren.add(new Klausur("RDB", 56, false, LocalDate.now().toString(), "10:00", "10:00"));
        alleKlausuren.add(new Klausur("AlDat", 78, false, LocalDate.now().minusDays(2).toString(), "10:00", "10:00"));

        return alleKlausuren;

    }

    @Test
    @DisplayName("Testet, dass nur Klausuren in der Zukunft geladen werden")
    void klausurenUngueltig() {
        //arrange
        Set<Klausur> alleKlausuren = arrange();
        KlausurService klausurService = new KlausurService();
        //act
        Set<Klausur> gueltigeKlausuren = klausurService.klausurIstNochImAnmeldezeitraum(alleKlausuren);
        Set<Long> lsfIDs = new TreeSet<>();
        for(Klausur klausur : gueltigeKlausuren) {
            lsfIDs.add(klausur.getLsfid());
        }
        //assert
        assertThat(gueltigeKlausuren).hasSize(2);

        assertThat(lsfIDs).contains(12L);
        assertThat(lsfIDs).contains(34L);
    }


    @Test
    @DisplayName("Testet ob nur gültige Klausuren auf true gesetzt werden")
    void klausurenStornierbar() {
        //arrange
        Set<Klausur> alleKlausuren = arrange();
        KlausurService klausurService = new KlausurService();
        //act
        Map<Klausur, Boolean> stornierbareKlausuren = klausurService.stornierbareKlausuren(alleKlausuren);

        //assert
        assertThat(stornierbareKlausuren).hasSize(4);

        for(Klausur klausur : stornierbareKlausuren.keySet()) {
            if(klausur.getLsfid() == 12 || klausur.getLsfid() == 34) {
                assertThat(stornierbareKlausuren.get(klausur)).isEqualTo(true);
            }
            else if (klausur.getLsfid() == 56 || klausur.getLsfid() == 78) {
                assertThat(stornierbareKlausuren.get(klausur)).isEqualTo(false);
            }
        }
    }

    @Test
    @Disabled
    @DisplayName("Klausurvalidierung: ")
    void klausurValidierung() {
        KlausurService klausurService = new KlausurService();
        Klausur klausur = new Klausur("RA", 1234, true, "1000-11-12", "11:00", "12:00");
        String beginn     = "";
        String ende       = "";
        String startdatum = "";
        String enddatum   = "";

        //klausurService.validiereKlausur(klausur, beginn, ende, startdatum, enddatum);

        //assertThat
    }

}


