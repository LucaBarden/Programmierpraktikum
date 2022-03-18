package de.propra.chicken.dto;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;
@Table("klausur")
public class KlausurDTO {
    @Id
    private long lsfID;
    private String veranstaltung;
    private boolean praesenz;
    private LocalDate date;
    private LocalTime beginn;
    private LocalTime end;

    public KlausurDTO(long lsfID, String veranstaltung, boolean praesenz, LocalDate date, LocalTime beginn, LocalTime end) {
        this.lsfID = lsfID;
        this.veranstaltung = veranstaltung;
        this.praesenz = praesenz;
        this.date = date;
        this.beginn = beginn;
        this.end = end;
    }

    public long getLsfID() {
        return lsfID;
    }

    public String getVeranstaltung() {
        return veranstaltung;
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
    public String toString() {
        return "KlausurDTO{" +
                "lsfID=" + lsfID +
                ", veranstaltung='" + veranstaltung + '\'' +
                ", praesenz=" + praesenz +
                ", date=" + date +
                ", beginn=" + beginn +
                ", end=" + end +
                '}';
    }


}
