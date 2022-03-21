package de.propra.chicken.db;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;

@Table("KLAUSUR")
public class KlausurDTO implements Persistable {

    @Id
    private Long lsf_id;
    private String name;
    private boolean praesenz;
    private LocalDate datum;
    private LocalTime beginn;
    private LocalTime ende;

    @Transient
    private boolean isNew = true;

    public KlausurDTO(Long lsf_id, String name, boolean praesenz, LocalDate datum, LocalTime beginn, LocalTime ende) {
        this.lsf_id = lsf_id;
        this.name = name;
        this.praesenz = praesenz;
        this.datum = datum;
        this.beginn = beginn;
        this.ende = ende;
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

    public LocalTime getEnde() {
        return ende;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public String toString() {
        return "KlausurDTO{" +
                "lsfID=" + lsf_id +
                ", veranstaltung='" + name + '\'' +
                ", praesenz=" + praesenz +
                ", date=" + datum +
                ", beginn=" + beginn +
                ", ende=" + ende +
                '}';
    }


    @Override
    public Object getId() {
        return lsf_id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
