package de.propra.chicken.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("klausur_student")
public class KlausurRef {


    @Override
    public int hashCode() {
        return Objects.hash(lsfID);
    }

    @Id
    @Column("klausur")
    private long lsfID;

    public KlausurRef(long lsfID) {
        this.lsfID = lsfID;
    }

    public long getLsfID() {
        return lsfID;
    }

    public KlausurRef() {

    }

    @Override
    public String toString() {
        return "KlausurRef{" +
                "lsfID=" + lsfID +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KlausurRef that = (KlausurRef) o;
        return lsfID == that.lsfID;
    }
}
