package de.propra.chicken.domain.model;

import java.util.Objects;

public class LsfID {

    private int id;

    public LsfID(int id){
        if (check(id)) this.id = id;
        else this.id = -1;

    }

    private boolean check(int id) {
        // TODO
        return true;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return Integer.toString(this.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LsfID lsfID = (LsfID) o;
        return id == lsfID.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
