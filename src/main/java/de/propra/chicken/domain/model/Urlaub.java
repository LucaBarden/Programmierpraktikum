package de.propra.chicken.domain.model;


import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;


public class Urlaub {

    private final LocalDate tag;
    private final LocalTime beginn;
    private final LocalTime end;

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

    public long duration() {
        return Duration.between(beginn, end).toMinutes();
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
