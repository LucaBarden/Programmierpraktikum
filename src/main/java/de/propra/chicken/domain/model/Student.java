package de.propra.chicken.domain.model;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class Student {

    private final long githubID;
    private int resturlaub;

    private Set<Urlaub> urlaube = new HashSet<>();
    private Set<KlausurRef> klausuren = new HashSet<>();

    public Student(long githubID) {
        this.githubID = githubID;
        this.resturlaub = 240;
    }

    public void setResturlaub(int resturlaub) {
        this.resturlaub = resturlaub;
    }

    public void setUrlaube(Set<Urlaub> urlaube) {
        this.urlaube = urlaube;
    }

    public void setKlausuren(Set<KlausurRef> klausuren) {
        this.klausuren = klausuren;
    }

    public void addKlausur(KlausurRef klausur) {
        this.klausuren.add(klausur);
    }

    public void addUrlaube(Set<Urlaub> urlaub) {
        urlaube.addAll(urlaub);
    }


    public Set<Urlaub> getUrlaube() {
        return urlaube;
    }

    public void checkAufGrundregeln(Urlaub urlaub) throws Exception {
        //Startzeit ist vor Endzeit
        if(urlaub.getVon().isAfter(urlaub.getBis())) {
            throw new Exception("Die Startzeit kann nicht nach der Endzeit liegen");
        }
        //Startzeit und Endzeit sind gleich
        if(urlaub.getVon().equals(urlaub.getBis())) {
            throw new Exception("Die Startzeit und Endzeit sind gleich!!");
        }
        //ganze Viertelstunden & Startzeiten 00, 15, 30, 45
        if (!(urlaub.getVon().getMinute() % 15 == 0) || !(urlaub.getBis().getMinute() % 15 == 0)) {
            throw new Exception("Die Start- und Endzeit muss ein Vielfaches von 15 Minuten sein");
        }
        // Resturlaub >= Urlaubszeit soll gelten
        if (this.resturlaub < Duration.between(urlaub.getVon(), urlaub.getBis()).toMinutes()) {
            throw new Exception("Es ist zu wenig Resturlaub übrig");
        }
    }

}
