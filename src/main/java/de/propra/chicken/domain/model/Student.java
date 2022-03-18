package de.propra.chicken.domain.model;

import de.propra.chicken.db.dto.StudentDTO;
import org.springframework.data.annotation.Id;

import java.time.Duration;
import java.time.LocalTime;
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

    public Student(long githubID, int resturlaub) {
        this.githubID = githubID;
        this.resturlaub = resturlaub;
    }

    public int getResturlaub() {
        return resturlaub;
    }

    public long getGithubID() {
        return githubID;
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

    public Set<Urlaub> urlaubSelberTag(Urlaub urlaub) {
        Set<Urlaub> uSelberTag = new HashSet<>();
        for (Urlaub tmpUrlaub : urlaube) {
            if (tmpUrlaub.getTag().compareTo(urlaub.getTag()) == 0) {
                uSelberTag.add(tmpUrlaub);
            }
        }
        return uSelberTag;
    }

    public void checkAufGrundregeln(Urlaub urlaub, String beginn, String ende) throws Exception {
        //Fehler: Startzeit ist nach Endzeit
        if(urlaub.getBeginn().isAfter(urlaub.getEnd())) {
            throw new Exception("Die Startzeit kann nicht nach der Endzeit liegen");
        }
        //Fehler: Startzeit und Endzeit sind gleich
        if(urlaub.getBeginn().equals(urlaub.getEnd())) {
            throw new Exception("Die Startzeit und Endzeit sind gleich!!");
        }
        //Fehler: keine ganzen Viertelstunden & Startzeiten 00, 15, 30, 45
        if (!(urlaub.getBeginn().getMinute() % 15 == 0) || !(urlaub.getEnd().getMinute() % 15 == 0)) {
            throw new Exception("Die Start- und Endzeit muss ein Vielfaches von 15 Minuten sein");
        }
        //Fehler: Resturlaub < Urlaubszeit
        if (this.resturlaub < Duration.between(urlaub.getBeginn(), urlaub.getEnd()).toMinutes()) {
            throw new Exception("Es ist zu wenig Resturlaub übrig");
        }
        if(urlaub.getBeginn().isBefore(LocalTime.parse(beginn)) || urlaub.getEnd().isAfter(LocalTime.parse(ende))) {
            throw new Exception("Der Urlaub muss im Praktikumszeitraum liegen");
        }
    }



}
