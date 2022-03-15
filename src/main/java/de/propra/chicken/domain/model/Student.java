package de.propra.chicken.domain.model;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Student {

    private final long studentID;
    private int resturlaub;

    private Set<Urlaub> urlaube = new HashSet<>();
    private Set<Klausur> klausuren = new HashSet<>();

    public Student(long githubID) {
        this.studentID = githubID;
        this.resturlaub = 240;
    }

    public void setResturlaub(int resturlaub) {
        this.resturlaub = resturlaub;
    }

    public void setUrlaube(Set<Urlaub> urlaube) {
        this.urlaube = urlaube;
    }

    public void setKlausuren(Set<Klausur> klausuren) {
        this.klausuren = klausuren;
    }

    public void addKlausur(Klausur klausur) {
        this.klausuren.add(klausur);
    }

    public void validiereUrlaub(Urlaub urlaub) throws Exception {
        //TODO validiere Urlaub
        Set<Urlaub> urlaubeSelberTag = new HashSet<>();
        Set<Klausur> klausurSelberTag = new HashSet<>();

        //ganze Viertelstunden & Startzeiten 00, 15, 30, 45
        if(!(urlaub.getVon().getMinute() % 15 == 0) || !(urlaub.getBis().getMinute() % 15 == 0)) {
            throw new Exception("Die Start- und Endzeit muss ein Vielfaches von 15 Minuten sein");
        }
        // Resturlaub >= Urlaubszeit soll gelten
        if( this.resturlaub < Duration.between(urlaub.getVon(), urlaub.getBis()).toMinutes()) {
            throw new Exception("Es ist zu wenig Resturlaub übrig");
        }

        for(Urlaub tmpUrlaub : urlaube) {
            if(tmpUrlaub.getTag().compareTo(urlaub.getTag()) == 0) {
                urlaubeSelberTag.add(tmpUrlaub);
            }
        }
        for(Klausur tmpKlausur : klausuren) {
            if(tmpKlausur.getDate().compareTo(urlaub.getTag()) == 0) {
                klausurSelberTag.add(tmpKlausur);
            }
        }
        if(!klausurSelberTag.isEmpty()) {
            //eine Klausur ist am selben Tag: Urlaub darf frei eingeteilt werden
            //überschneidende Urlaubszeit erstatten
        }
        else { //keine Klausur am selben Tag
            if(urlaubeSelberTag.isEmpty()) {
                //der anzulegende Urlaub ist der erste Urlaubsblock an dem Tag: den ganzen Tag frei oder max 2,5 Stunden
            }
            else if(urlaubeSelberTag.size() == 1) {
                //schon 1 Urlaub am selben Tag: Blöcke müssen am Anfang und Ende liegen mit mind. 90 Min Arbeitszeit dazwischen
            }
            else if(urlaubeSelberTag.size() == 2) {
                //schon 2 Urlaube am selben Tag: kein weiterer Urlaub kann gebucht werden
            }
        }
    }

    public void validiereKlausurAnmeldung(Klausur klausur) throws Exception {
        //Die klausur ist mindestens einen Tag später
        if(!klausur.getDate().isAfter(LocalDate.now())) {
            throw new Exception("Klausur findet heute statt. Anmeldung nicht mehr moeglich");
        }
        //Überprüft, ob der Student schon angemeldet ist
        for(Klausur tmpKlausur : this.klausuren) {
            if(tmpKlausur.getLsfid() == klausur.getLsfid()) {
                throw new Exception("Du bist bereits bei der Klausur angemeldet");
            }
        }

    }

    public void addUrlaub(Urlaub urlaub) {
        urlaube.add(urlaub);
    }
}
