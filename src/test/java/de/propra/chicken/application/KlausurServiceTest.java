package de.propra.chicken.application;

import de.propra.chicken.application.service.KlausurService;
import de.propra.chicken.application.service.repository.KlausurRepository;
import de.propra.chicken.domain.model.Klausur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class KlausurServiceTest {

    @Test
    @DisplayName("Testet ob eine ungueltige LSF ID einen Fehler wirft")
    public void invalidLSFID(){
        //TODO: (in)validLsfID aus DB oder Internet je eigene Methode
        KlausurRepository repo = mock(KlausurRepository.class);
        KlausurService service = new KlausurService(repo);
        doNothing().when(repo).save(any());

        assertThrows(Exception.class, () -> {
            service.save(new Klausur("Test", 123, false, null, null, null));
        });
    }

    @Test
    @DisplayName("Testet ob eine gueltige LSF ID keinen Fehler wirft")
    public void validLSFID(){
        KlausurRepository repo = mock(KlausurRepository.class);
        KlausurService service = new KlausurService(repo);
        doNothing().when(repo).save(any());

        assertDoesNotThrow(() ->
            service.save(new Klausur("Test", 12345, false, null, null, null)));
    }

    @Test
    @DisplayName("Testet ob ein gueltiger Name keinen Fehler wirft")
    public void validName(){
        //TODO: mocken für fake-Internet-Abfrage
        KlausurRepository repo = mock(KlausurRepository.class);
        KlausurService service = new KlausurService(repo);
        doNothing().when(repo).save(any());

        assertDoesNotThrow(() ->
                service.save(new Klausur("Rechnerarchitektur", 219468, false, null, null, null)));
    }

    @Test
    @DisplayName("Testet ob ein ungueltiger Name einen Fehler wirft")
    public void invalidName(){
        KlausurRepository repo = mock(KlausurRepository.class);
        KlausurService service = new KlausurService(repo);
        doNothing().when(repo).save(any());

        assertThrows(Exception.class, () ->
                service.save(new Klausur("Rechnerarchitektur123", 219468, false, null, null, null)));
    }


}
