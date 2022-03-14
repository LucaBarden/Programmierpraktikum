package de.propra.chicken.domain.model;

import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;


public class Urlaub {

    private long githubID;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) //TODO eigentlich weg
    private LocalDate tag;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime von;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime bis;

    public Urlaub(LocalDate tag, LocalTime von, LocalTime bis) {
        this.tag = tag;
        this.von = von;
        this.bis = bis;
    }

    public LocalDate getTag() {
        return tag;
    }

    public LocalTime getVon() {
        return von;
    }

    public LocalTime getBis() {
        return bis;
    }

}
