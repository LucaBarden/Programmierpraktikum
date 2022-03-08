package de.propra.chicken.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Klausur {
    private String veranstaltung;
    private int lsfid;
    private boolean praesenz;
    private LocalDate date;
    private LocalTime beginn;
    private LocalTime end;

    public Klausur(String veranstaltung, int lsfid, boolean praesenz, LocalDate date, LocalTime beginn, LocalTime end) {
        this.veranstaltung = veranstaltung;
        this.lsfid = lsfid;
        this.praesenz = praesenz;
        this.date = date;
        this.beginn = beginn;
        this.end = end;
    }

    public Klausur() {
    }

    public String getVeranstaltung() {
        return veranstaltung;
    }

    public int getLsfid() {
        return lsfid;
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
}