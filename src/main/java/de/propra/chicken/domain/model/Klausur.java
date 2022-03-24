package de.propra.chicken.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;


public class Klausur {
    private final String name;
    private final long lsfid;
    private final boolean praesenz;
    private final LocalDate datum;
    private final LocalTime beginn;
    private final LocalTime ende;

    public Klausur(String name, long lsfid, boolean praesenz, String datum, String beginn, String ende) {
        this.name = name;
        this.lsfid = lsfid;
        this.praesenz = praesenz;
        this.datum = LocalDate.parse(datum);
        this.beginn = LocalTime.parse(beginn);
        this.ende = LocalTime.parse(ende);
    }

    public String getName() {
        return name;
    }

    public long getLsfid() {
        return this.lsfid;
    }

    public boolean isPraesenz() {
        return praesenz;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public LocalTime getBeginn() {
        return beginn;
    }

    public LocalTime getEnde() {
        return ende;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Klausur klausur = (Klausur) o;
        return Objects.equals(lsfid, klausur.lsfid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lsfid);
    }

    public KlausurData getKlausurData(){
        return new KlausurData(this.datum, this.beginn, this.ende, this.praesenz);
    }

    public KlausurRef getKlausurRef(){
        return new KlausurRef(this.lsfid);
    }

    @Override
    public String toString() {
        return ((praesenz) ? "Präsenklausur " : "Onlineklausur ") + this.name + "(" + this.getDatum().toString() + ", "
                + this.getBeginn().truncatedTo(ChronoUnit.MINUTES).toString() + " - "
                + this.getEnde().truncatedTo(ChronoUnit.MINUTES).toString() + " Uhr)";
    }
}