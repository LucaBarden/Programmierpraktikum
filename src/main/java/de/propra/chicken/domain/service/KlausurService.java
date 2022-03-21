package de.propra.chicken.domain.service;

import de.propra.chicken.domain.model.Klausur;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
@Service
public class KlausurService {

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
        //prüft, ob die Klausur noch im stornierbaren Zeitraum ist
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
        if(klausur.getBeginn().isAfter(klausur.getEnd())) {
            throw new Exception("Die Startzeit kann nicht nach der Endzeit liegen");
        }
        if(klausur.getBeginn().equals(klausur.getEnd())) {
            throw new Exception("Die Startzeit und Endzeit sind gleich!!");
        }
        /*if(Duration.between(klausur.getBeginn(), klausur.getEnd()).toMinutes() < 60) {
            throw new Exception("Die Klausur muss mindestens 60 Minuten dauern.");
        }*/
    }
}
