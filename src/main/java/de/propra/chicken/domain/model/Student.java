package de.propra.chicken.domain.model;

import de.propra.chicken.domain.service.Validierung;

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

    public void validiereUrlaub(Urlaub urlaub) {
        //TODO validiere Urlaub
        /*
        - ganze Viertelstunden & Startzeiten 00, 15, 30, 45
        - Buchungszeit <= Resturlaub
        - bei angemeldeter Klausur, überschneidende Urlaubszeit erstatten
        - ein Block pro Tag: - den ganzen Tag frei
                             - max 2,5 Stunden frei
        - zwei Blöcke pro Tag: - müssen am Anfang und am Ende sein
                               - mindestens 90 Min Arbeitszeit zwischen dem Urlaub
        - bei Klausuranmeldung an dem Tag: Urlaub darf frei eingeteilt werden
         */
    }

    public void validiereKlausurAnmeldung(Klausur klausur) throws Exception {

        try {
            //Die klausur ist mindestens einen Tag später
            Validierung.klausurAnmeldung(klausur);
        }
        catch(Exception ex) {
            throw ex;
        }
        //Überprüft, ob der Student schon angemeldet ist
        for(Klausur tmpKlausur : this.klausuren) {
            if(tmpKlausur.getLsfid() == klausur.getLsfid()) {
                throw new Exception("Du bist bereits bei der Klausur angemeldet");
            }
        }

    }

    public void addKlausur(Klausur klausur) {
        this.klausuren.add(klausur);
    }
}
