package de.propra.chicken.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class KlausurRef {
    private final long id;
    private LocalDate tag;
    private LocalTime von;
    private LocalTime bis;
    private boolean isPraesenz;

    public KlausurRef(long id) {
        this.id = id;
    }

    public KlausurRef(long id, LocalDate tag, LocalTime von, LocalTime bis) {
        this.id = id;
        this.tag = tag;
        this.von = von;
        this.bis = bis;
    }

    public KlausurRef(long id, LocalDate tag, LocalTime von, LocalTime bis, boolean isPraesenz) {
        this.id = id;
        this.tag = tag;
        this.von = von;
        this.bis = bis;
        this.isPraesenz = isPraesenz;
    }

    public KlausurRef(int id, String tag, String von, String bis, boolean isPraesenz) {
        this.id  = id;
        this.tag = LocalDate.parse(tag);
        this.von = LocalTime.parse(von);
        this.bis = LocalTime.parse(bis);
        this.isPraesenz = isPraesenz;
    }

    public long getId() {
        return id;
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


    public boolean isPraesenz() {
        return isPraesenz;
    }
}
