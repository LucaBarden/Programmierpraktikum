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
        /*int puffer;
        if(klausur.isPraesenz()) { puffer = 30; }
        else { puffer = 120; }*/

        if(klausur.getBeginn().isAfter(klausur.getEnd())) {
            throw new Exception("Die Startzeit kann nicht nach der Endzeit liegen");
        }
        if(klausur.getBeginn().equals(klausur.getEnd())) {
            throw new Exception("Die Startzeit und Endzeit sind gleich!!");
        }
        /*if(Duration.between(klausur.getBeginn(), klausur.getEnd()).toMinutes() < 60) {
            throw new Exception("Die Klausur muss mindestens 60 Minuten dauern.");
        }*/
        //es wird nicht überprüft, ob die Klausur am Wochenende ist. notwendig?
        /*if(klausur.getDatum().isBefore(LocalDate.parse(startdatum))) {
            throw new Exception("Das Klausurdatum liegt vor Beginn des Praktikums.");
        }
        if(klausur.getDatum().isAfter(LocalDate.parse(enddatum))) {
            throw new Exception("Das Klausurdatum liegt nach Ende des Praktikums.");
        }
        if(klausur.getBeginn().minusMinutes(puffer).isBefore(LocalTime.parse(beginn)) || klausur.getBeginn().minusMinutes(puffer).equals(LocalTime.parse(beginn))) {
            throw new Exception("Der freizustellende Zeitraum für die Klausur liegt vor dem Praktikumszeitraum.");
        }
        if(klausur.getEnd().plusMinutes(puffer).isAfter(LocalTime.parse(ende)) || klausur.getEnd().plusMinutes(puffer).equals(LocalTime.parse(ende))) {
            throw new Exception("Der freizustellende Zeitraum für die Klausur liegt nach dem Praktikumszeitraum.");
        }*/
    }
}
