package de.propra.chicken.application;

import de.propra.chicken.application.service.KlausurService;
import de.propra.chicken.application.service.repository.KlausurRepository;
import de.propra.chicken.domain.model.Klausur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class KlausurServiceTest {

    
    @Test
    @DisplayName("Testet ob eine ungueltige LSF ID einen Fehler wirft")
    public void validLSFID(){
        KlausurRepository repo = mock(KlausurRepository.class);
        KlausurService service = new KlausurService(repo);
        doNothing().when(repo).save(any());

        assertThrows(Exception.class, () -> {
            service.save(new Klausur("Test", 123, false, null, null, null));
        });
    }


}
