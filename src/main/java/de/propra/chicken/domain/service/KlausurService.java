package de.propra.chicken.domain.service;

import de.propra.chicken.domain.model.Klausur;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

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

    public void validiereKlausur(Klausur klausur, String beginn, String ende, String anfangsdatum, String enddatum) throws Exception {
        if(klausur.getBeginn().isAfter(klausur.getEnde())) {
            throw new Exception("Die Startzeit kann nicht nach der Endzeit liegen");
        }
        if(klausur.getBeginn().equals(klausur.getEnde())) {
            throw new Exception("Die Startzeit und Endzeit sind gleich!!");
        }
        if(klausur.getDatum().isBefore(LocalDate.parse(anfangsdatum))) {
            throw new Exception("Das Klausurdatum liegt vor dem Praktikumsbeginn!");
        }
        if(klausur.getDatum().isAfter(LocalDate.parse(enddatum))) {
            throw new Exception("Das Klausurdatum liegt nach dem Praktikumsende!");
        }
        if(klausur.getBeginn().isBefore(LocalTime.parse(beginn))) {
            throw new Exception("Setzen Sie den Anfang der Klausur auf den Anfang des Praktikums.");
        }
        if(klausur.getEnde().isAfter(LocalTime.parse(ende))) {
            throw new Exception("Setzen Sie das Ende der Klausur auf das Ende des Praktikums.");
        }
        /*if(Duration.between(klausur.getBeginn(), klausur.getEnd()).toMinutes() < 60) {
            throw new Exception("Die Klausur muss mindestens 60 Minuten dauern.");
        }*/
    }

    public void validiereLsfIdInternet(Klausur klausur) throws Exception {
       String webContent = Jsoup.connect(String.format("https://lsf.hhu.de/qisserver/rds?state=verpublish&status=init&vmfile=no&publishid=%s&moduleCall=webInfo" +
               "&publishConfFile=webInfo&publishSubDir=veranstaltung", klausur.getLsfid())).get().text();
       if (!(webContent.contains("VeranstaltungsID"))) {
           throw new IllegalArgumentException("Invalide LSF ID");
       }
   }
}
