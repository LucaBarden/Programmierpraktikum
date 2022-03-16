package de.propra.chicken.application;

import de.propra.chicken.application.service.Service;
import de.propra.chicken.application.service.repo.IRepository;
import de.propra.chicken.domain.model.Klausur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ServiceTest {

    @Test
    @DisplayName("Testet ob eine ungueltige LSF ID einen Fehler wirft")
    public void invalidLSFID(){
        //TODO: (in)validLsfID aus DB oder Internet je eigene Methode
        IRepository repo = mock(IRepository.class);
        Service service = new Service(repo);
        doNothing().when(repo).speicherKlausur(any());

        assertThrows(Exception.class, () -> {
            service.speicherKlausur(new Klausur("Test", 123, false, null, null, null));
        });
    }

    @Test
    @DisplayName("Testet ob eine gueltige LSF ID keinen Fehler wirft")
    public void validLSFID(){
        IRepository repo = mock(IRepository.class);
        Service service = new Service(repo);
        doNothing().when(repo).speicherKlausur(any());

        assertDoesNotThrow(() ->
            service.speicherKlausur(new Klausur("Test", 12345, false, null, null, null)));
    }

    @Test
    @DisplayName("Klausuranmeldung ")
    public void klausurAnmeldungTest() {

    }



}


