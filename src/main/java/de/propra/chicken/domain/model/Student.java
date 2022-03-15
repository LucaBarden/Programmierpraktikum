package de.propra.chicken.domain.model;

import java.util.Set;

public class Student {

    private final long studentID;
    private int resturlaub;

    private Set<Urlaub> urlaube;
    private Set<Klausur> klausuren;

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
    }

    public static void validiereKlausurAnmeldung(Klausur klausur) {
        //TODO validieren
    }
}
