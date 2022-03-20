package de.propra.chicken.domain.model;


import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;


public class Urlaub {

    @Id
    private int id;
    private LocalDate tag;
    private LocalTime beginn;
    private LocalTime end;

    public Urlaub(String tag, String beginn, String end) {
        this.tag = LocalDate.parse(tag);
        this.beginn = LocalTime.parse(beginn);
        this.end = LocalTime.parse(end);
    }

    public LocalDate getTag() {
        return tag;
    }

    public LocalTime getBeginn() {
        return beginn;
    }

    public LocalTime getEnd() {
        return end;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Urlaub urlaub = (Urlaub) o;
        return Objects.equals(tag, urlaub.tag) && Objects.equals(beginn, urlaub.beginn) && Objects.equals(end, urlaub.end);
    }

    @Override
    public String toString() {
        return "Urlaub{" +
                "tag=" + tag +
                ", von=" + beginn +
                ", bis=" + end +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, beginn, end);
    }

}
