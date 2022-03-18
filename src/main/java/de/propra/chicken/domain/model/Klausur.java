package de.propra.chicken.domain.model;

import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;


public class Klausur {
    private String name;
    private long lsfid;
    private boolean praesenz;
    private LocalDate datum;
    private LocalTime beginn;
    private LocalTime end;

    public Klausur(String name, long lsfid, boolean praesenz, String datum, String beginn, String end) {
        this.name = name;
        this.lsfid = lsfid;
        this.praesenz = praesenz;
        this.datum = LocalDate.parse(datum);
        this.beginn = LocalTime.parse(beginn);
        this.end = LocalTime.parse(end);
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

    public LocalTime getEnd() {
        return end;
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
        return new KlausurData(this.datum, this.beginn, this.end, this.praesenz);
    }

    public KlausurRef getKlausurRef(){
        return new KlausurRef(this.lsfid);
    }

    @Override
    public String toString() {
        return this.name + "(" + this.getDatum().toString() + ", "
                + this.getBeginn().truncatedTo(ChronoUnit.MINUTES).toString() + " - "
                + this.getEnd().truncatedTo(ChronoUnit.MINUTES).toString() + " Uhr)";
    }
}