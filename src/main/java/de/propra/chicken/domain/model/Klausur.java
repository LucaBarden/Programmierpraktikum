package de.propra.chicken.domain.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;


public class Klausur {
    private String veranstaltung;
    private int lsfid;
    private boolean praesenz;
    private LocalDate date;
    private LocalTime beginn;
    private LocalTime end;

    public Klausur(String veranstaltung, int lsfid, boolean praesenz, String date, String beginn, String end) {
        this.veranstaltung = veranstaltung;
        this.lsfid = lsfid;
        this.praesenz = praesenz;
        this.date = LocalDate.parse(date);
        this.beginn = LocalTime.parse(beginn);
        this.end = LocalTime.parse(end);
    }

    public String getVeranstaltung() {
        return veranstaltung;
    }

    public int getLsfid() {
        return this.lsfid;
    }

    public boolean isPraesenz() {
        return praesenz;
    }

    public LocalDate getDate() {
        return date;
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
}