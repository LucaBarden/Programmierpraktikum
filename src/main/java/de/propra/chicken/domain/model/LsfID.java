package de.propra.chicken.domain.model;

public class LsfID {

    private int id;

    public LsfID(int id) {
        if (check(id)) this.id = id;
        else this.id = -1;

    }

    private boolean check(int id) {
        // TO-DO
        return true;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return Integer.toString(this.id);
    }
}
