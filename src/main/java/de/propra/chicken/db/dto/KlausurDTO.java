package de.propra.chicken.db.dto;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Table("klausur")
public class KlausurDTO {

    @Id
    private Long ID;
    private Long lsf_id;
    private String name;
    private boolean praesenz;
    private LocalDate datum;
    private LocalTime beginn;
    private LocalTime end;

    public KlausurDTO(Long lsf_id, String name, boolean praesenz, LocalDate datum, LocalTime beginn, LocalTime end) {
        this.lsf_id = lsf_id;
        this.name = name;
        this.praesenz = praesenz;
        this.datum = datum;
        this.beginn = beginn;
        this.end = end;
    }

    public Long getLsfID() {
        return lsf_id;
    }

    public String getName() {
        return name;
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
    public String toString() {
        return "KlausurDTO{" +
                "lsfID=" + lsf_id +
                ", veranstaltung='" + name + '\'' +
                ", praesenz=" + praesenz +
                ", date=" + datum +
                ", beginn=" + beginn +
                ", end=" + end +
                '}';
    }


}
