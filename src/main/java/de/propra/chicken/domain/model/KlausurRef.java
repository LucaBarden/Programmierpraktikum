package de.propra.chicken.domain.model;

import org.springframework.data.annotation.Id;

public class KlausurRef {
    @Id
    private long lsfID;

    public KlausurRef(long lsfID) {
        this.lsfID = lsfID;
    }

    public long getLsfID() {
        return lsfID;
    }

}
