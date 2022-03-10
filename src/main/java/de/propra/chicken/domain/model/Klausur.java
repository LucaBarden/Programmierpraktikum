package de.propra.chicken.domain.model;



import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;


public class Klausur {
    private String veranstaltung;
    private LsfID lsfid;
    private boolean praesenz;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime beginn;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime end;

    public Klausur(String veranstaltung, int lsfid, boolean praesenz, LocalDate date, LocalTime beginn, LocalTime end) {
        this.veranstaltung = veranstaltung;
        this.lsfid = new LsfID(lsfid);
        this.praesenz = praesenz;
        this.date = date;
        this.beginn = beginn;
        this.end = end;
    }

    public String getVeranstaltung() {
        return veranstaltung;
    }

    public int getLsfid() {
        return this.lsfid.getId();
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