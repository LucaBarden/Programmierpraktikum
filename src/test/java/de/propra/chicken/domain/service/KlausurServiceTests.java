package de.propra.chicken.domain.service;

import de.propra.chicken.domain.model.Klausur;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class KlausurServiceTests {

    private static final String BEGINN_PRAKTIKUM = "08:30";
    private static final String ENDE_PRAKTIKUM   = "12:30";
    private static final String STARTDATUM = LocalDate.of(2022,3,25).toString();
    private static final String ENDDATUM   = LocalDate.of(2022,4,29).toString();
    private static final MockedStatic<Jsoup> jsoupMockedStatic = mockStatic(Jsoup.class);
    private final static LocalDate DATE = LocalDate.of(2022, 4, 1);
    private static final Clock clock = Clock.fixed(DATE.atStartOfDay(ZoneId.systemDefault()).toInstant(),ZoneId.systemDefault());


    Set<Klausur> arrange(){
        Set<Klausur> alleKlausuren = new HashSet<>();
        //gueltig
        alleKlausuren.add(new Klausur("Rechnerarchitektur", 12, true, LocalDate.now(clock).plusDays(1).toString(), "10:00", "10:00"));
        alleKlausuren.add(new Klausur("Progra", 34, false, LocalDate.now(clock).plusDays(3).toString(), "10:00", "10:00"));
        //ungueltig
        alleKlausuren.add(new Klausur("RDB", 56, false, LocalDate.now(clock).toString(), "10:00", "10:00"));
        alleKlausuren.add(new Klausur("AlDat", 78, false, LocalDate.now(clock).minusDays(2).toString(), "10:00", "10:00"));

        return alleKlausuren;

    }

    @Test
    @DisplayName("Testet, dass nur Klausuren in der Zukunft geladen werden")
    void klausurenUngueltig() {
        //arrange
        Set<Klausur> alleKlausuren = arrange();
        KlausurService klausurService = new KlausurService(clock);
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
        KlausurService klausurService = new KlausurService(clock);
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
    @DisplayName("Fehler, wenn startzeit vor Endzeit der Klausur ist")
    void klausurValidierung1() {
        KlausurService klausurService = new KlausurService(clock);
        Klausur klausur = new Klausur("RA", 1234, true, "1000-11-12", "11:00", "10:00");

        Exception thrown = assertThrows(Exception.class, () -> klausurService.validiereKlausur(klausur, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));

        assertThat(thrown.getMessage()).isEqualTo("Die Startzeit kann nicht nach der Endzeit liegen");
    }

    @Test
    @DisplayName("Fehler, wenn Startzeit und Endzeit der Klausur gleich sind")
    void klausurValidierung2() {
        KlausurService klausurService = new KlausurService(clock);
        Klausur klausur = new Klausur("RA", 1234, true, "1000-11-12", "11:00", "11:00");

        Exception thrown = assertThrows(Exception.class, () -> klausurService.validiereKlausur(klausur, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));

        assertThat(thrown.getMessage()).isEqualTo("Die Startzeit und Endzeit sind gleich!!");
    }

    @Test
    @DisplayName("Fehler, wenn Klausur beim validieren vor dem Praktikumsdatum liegt")
    void klausurValidierung3() {
        KlausurService klausurService = new KlausurService(clock);
        Klausur klausur = new Klausur("RA", 1234, true, LocalDate.now(clock).minusDays(9).toString(), "11:00", "11:30");

        Exception thrown = assertThrows(Exception.class, () -> klausurService.validiereKlausur(klausur, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));

        assertThat(thrown.getMessage()).isEqualTo("Das Klausurdatum liegt vor dem Praktikumsbeginn!");
    }

    @Test
    @DisplayName("Fehler, wenn Klausur beim validieren nach dem Praktikumsdatum liegt")
    void klausurValidierung4() {
        KlausurService klausurService = new KlausurService(clock);
        Klausur klausur = new Klausur("RA", 1234, true, LocalDate.parse(ENDDATUM).plusDays(1).toString(), "11:00", "11:30");

        Exception thrown = assertThrows(Exception.class, () -> klausurService.validiereKlausur(klausur, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));

        assertThat(thrown.getMessage()).isEqualTo("Das Klausurdatum liegt nach dem Praktikumsende!");
    }

    @Test
    @DisplayName("Fehler, wenn Klausurbeginn vor der Praktikumszeit liegt")
    void klausurValidierung5() {
        KlausurService klausurService = new KlausurService(clock);
        Klausur klausur = new Klausur("RA", 1234, true, LocalDate.now(clock).plusDays(1).toString(), "07:00", "08:00");

        Exception thrown = assertThrows(Exception.class, () -> klausurService.validiereKlausur(klausur, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));

        assertThat(thrown.getMessage()).isEqualTo("Setzen Sie den Anfang der Klausur auf den Anfang des Praktikums.");
    }

    @Test
    @DisplayName("Fehler, wenn Klausurende nach Praktikumszeitraum liegt")
    void klausurValidierung6() {
        KlausurService klausurService = new KlausurService(clock);
        Klausur klausur = new Klausur("RA", 1234, true, LocalDate.now(clock).plusDays(1).toString(), "13:00", "14:00");

        Exception thrown = assertThrows(Exception.class, () -> klausurService.validiereKlausur(klausur, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM, STARTDATUM, ENDDATUM));

        assertThat(thrown.getMessage()).isEqualTo("Setzen Sie das Ende der Klausur auf das Ende des Praktikums.");
    }

    @Test
    @DisplayName("Testet ob eine ungueltige LSF ID einen Fehler wirft")
    public void invalidLSFID() throws Exception {
        KlausurService klausurService = new KlausurService(clock);
        Klausur klausur = mock(Klausur.class);
        Document document = mock(Document.class);
        Connection connection = mock(Connection.class);
        jsoupMockedStatic.when(() -> Jsoup.connect(anyString())).thenReturn(connection);
        when(connection.get()).thenReturn(document);
        when(document.text()).thenReturn("invalide");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> klausurService.validiereLsfIdInternet(klausur));
        assertThat(exception.getMessage()).isEqualTo("Invalide LSF ID");
    }

    @Test
    @DisplayName("Testet ob eine gueltige LSF ID keinen Fehler wirft")
    public void validLSFID() throws Exception {
        KlausurService klausurService = new KlausurService(clock);
        Klausur klausur = mock(Klausur.class);
        Document document = mock(Document.class);
        Connection connection = mock(Connection.class);
        jsoupMockedStatic.when(() -> Jsoup.connect(anyString())).thenReturn(connection);
        when(connection.get()).thenReturn(document);
        when(document.text()).thenReturn("VeranstaltungsID");

        assertDoesNotThrow(() -> klausurService.validiereLsfIdInternet(klausur));
    }

}


