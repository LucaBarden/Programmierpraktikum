package de.propra.chicken.domain.service;

import de.propra.chicken.domain.model.Klausur;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
@Service
public class KlausurService {

    private static String BEGINN_PRAKTIKUM = "08:30";
    private static String ENDE_PRAKTIKUM   = "12:30";

    public  Set<Klausur> klausurIstNochImAnmeldezeitraum(Set<Klausur> alleKlausuren) {
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

    public void validiereKlausur(Klausur klausur) throws Exception {
        //Klausur muss mind. 60min dauern
        //Klausurende + puffer muss nach PR
    }
}
