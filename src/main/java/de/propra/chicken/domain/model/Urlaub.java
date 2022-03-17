package de.propra.chicken.domain.model;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;


public class Urlaub {

    private LocalDate tag;
    private LocalTime von;
    private LocalTime bis;

    public Urlaub(String tag, String von, String bis) {
        this.tag = LocalDate.parse(tag);
        this.von = LocalTime.parse(von);
        this.bis = LocalTime.parse(bis);
    }

    /*
     * um in der Urlaubsvalidierung .toString() zu sparen. sinnvoll?
    public Urlaub(LocalDate tag, LocalTime von, LocalTime bis) {
        this.tag = tag;
        this.von = von;
        this.bis = bis;
    }*/

    public LocalDate getTag() {
        return tag;
    }

    public LocalTime getVon() {
        return von;
    }

    public LocalTime getBis() {
        return bis;
    }

    public void setVon(LocalTime von) {
        this.von = von;
    }

    public void setBis(LocalTime bis) {
        this.bis = bis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Urlaub urlaub = (Urlaub) o;
        return Objects.equals(tag, urlaub.tag) && Objects.equals(von, urlaub.von) && Objects.equals(bis, urlaub.bis);
    }

}
