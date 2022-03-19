package de.propra.chicken.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("klausur_student")
public class KlausurRef {
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
}
