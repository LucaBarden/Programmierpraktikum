package de.propra.chicken.domain.service;

import de.propra.chicken.domain.model.Klausur;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
@Service
public class KlausurService {


    public  Set<Klausur> validiereAlleKlausuren(Set<Klausur> alleKlausuren) {
        Set<Klausur> gueltigeKlausuren = new HashSet<>();
        for(Klausur klausur : alleKlausuren) {
            if(klausur.getDatum().isAfter(LocalDate.now())) {
                gueltigeKlausuren.add(klausur);
            }
        }
        return gueltigeKlausuren;
    }

    public Map<Klausur, Boolean> stornierbareKlausuren(Set<Klausur> klausuren) {
        Map<Klausur, Boolean> stornierbar = new HashMap<>();
        for(Klausur klausur : klausuren) {
            if(klausur.getDatum().isAfter(LocalDate.now())) {
                stornierbar.put(klausur, true);
            }
            else {
                stornierbar.put(klausur, false);
            }
        }
        return stornierbar;
    }

}
