package de.propra.chicken.dto;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;
@Table("klausur")
public class KlausurDTO {

    @Id
    private Long ID;
    private Long lsfid;
    private String veranstaltung;
    private boolean praesenz;
    private LocalDate date;
    private LocalTime beginn;
    private LocalTime end;

    public KlausurDTO(Long lsfid, String veranstaltung, boolean praesenz, LocalDate date, LocalTime beginn, LocalTime end) {
        this.lsfid = lsfid;
        this.veranstaltung = veranstaltung;
        this.praesenz = praesenz;
        this.date = date;
        this.beginn = beginn;
        this.end = end;
    }

    public Long getLsfID() {
        return lsfid;
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
                "lsfID=" + lsfid +
                ", veranstaltung='" + veranstaltung + '\'' +
                ", praesenz=" + praesenz +
                ", date=" + date +
                ", beginn=" + beginn +
                ", end=" + end +
                '}';
    }


}
