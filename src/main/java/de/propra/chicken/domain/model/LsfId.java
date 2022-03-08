package de.propra.chicken.domain.model;

public class LsfId {

    private int id;

    public LsfId(int id) {
        this.id = id;
    }

    public LsfId() {
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString(){
        return Integer.toString(this.id);
    }
}
