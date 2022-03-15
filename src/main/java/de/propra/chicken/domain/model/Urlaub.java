package de.propra.chicken.domain.model;


import java.time.LocalDate;
import java.time.LocalTime;


public class Urlaub {

    private long githubID;

    private LocalDate tag;
    private LocalTime von;
    private LocalTime bis;

    public Urlaub(String tag, String von, String bis) {
        this.tag = LocalDate.parse(tag);
        this.von = LocalTime.parse(von);
        this.bis = LocalTime.parse(bis);
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
