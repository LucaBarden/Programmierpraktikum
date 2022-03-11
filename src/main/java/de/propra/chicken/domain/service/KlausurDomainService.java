package de.propra.chicken.domain.service;

import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;

import java.time.LocalDate;
import java.util.*;

public class KlausurDomainService {


    public static List<Klausur> validiereAlleKlausuren(List<Klausur> alleKlausuren) {
        //TODO So gemacht damit wir weniger Testfälle haben
        List<Klausur> gueltigeKlausuren = new LinkedList<>();
        for(Klausur klausur : alleKlausuren) {
            if(klausur.getDate().isAfter(LocalDate.now())) {
                gueltigeKlausuren.add(klausur);
            }
        }
        return gueltigeKlausuren;


    }

    public static Map<Klausur, Boolean> stornierbareKlausuren(List<Klausur> klausuren) {
        Map<Klausur, Boolean> stornierbar = new HashMap<>();
        for(Klausur klausur : klausuren) {
            if(klausur.getDate().isAfter(LocalDate.now())) {
                stornierbar.put(klausur, true);
            }
            else {
                stornierbar.put(klausur, false);
            }
        }
        return stornierbar;
    }
}
