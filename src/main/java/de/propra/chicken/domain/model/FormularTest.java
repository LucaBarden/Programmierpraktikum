package de.propra.chicken.domain.model;


public class FormularTest {
    private String veranstaltung;
    private int lsfid;
    private boolean praesenz;
    private int date;
    private int beginn;
    private int end;

    public FormularTest(String veranstaltung, int lsfid, boolean praesenz, int date, int beginn, int end) {
        this.veranstaltung = veranstaltung;
        this.lsfid = lsfid;
        this.praesenz = praesenz;
        this.date = date;
        this.beginn = beginn;
        this.end = end;
    }

    public String getVeranstaltung() {
        return veranstaltung;
    }

    public int getLsfid() {
        return this.lsfid;
    }

    public boolean isPraesenz() {
        return praesenz;
    }

    public int getDate() {
        return date;
    }

    public int getBeginn() {
        return beginn;
    }

    public int getEnd() {
        return end;
    }

}