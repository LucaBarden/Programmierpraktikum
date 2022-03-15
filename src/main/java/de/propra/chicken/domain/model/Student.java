package de.propra.chicken.domain.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class Student {

    private final long studentID;
    private int resturlaub;

    private Set<Urlaub> urlaube = new HashSet<>();
    private Set<Klausur> klausuren = new HashSet<>();

    public Student(long githubID) {
        this.studentID = githubID;
        this.resturlaub = 240;
    }

    public void setResturlaub(int resturlaub) {
        this.resturlaub = resturlaub;
    }

    public void setUrlaube(Set<Urlaub> urlaube) {
        this.urlaube = urlaube;
    }

    public void setKlausuren(Set<Klausur> klausuren) {
        this.klausuren = klausuren;
    }

    public void addKlausur(Klausur klausur) {
        this.klausuren.add(klausur);
    }

    public Urlaub validiereUrlaub(Urlaub urlaub) throws Exception {
        //TODO validiere Urlaub
        Set<Urlaub> urlaubeSelberTag = new HashSet<>();
        Set<Klausur> klausurSelberTag = new HashSet<>();
        Urlaub zuErstattenderUrlaub = null;

        //ganze Viertelstunden & Startzeiten 00, 15, 30, 45
        if(!(urlaub.getVon().getMinute() % 15 == 0) || !(urlaub.getBis().getMinute() % 15 == 0)) {
            throw new Exception("Die Start- und Endzeit muss ein Vielfaches von 15 Minuten sein");
        }
        // Resturlaub >= Urlaubszeit soll gelten
        if( this.resturlaub < Duration.between(urlaub.getVon(), urlaub.getBis()).toMinutes()) {
            throw new Exception("Es ist zu wenig Resturlaub übrig");
        }

        for(Urlaub tmpUrlaub : urlaube) {
            if(tmpUrlaub.getTag().compareTo(urlaub.getTag()) == 0) {
                urlaubeSelberTag.add(tmpUrlaub);
            }
        }
        for(Klausur tmpKlausur : klausuren) {
            if(tmpKlausur.getDate().compareTo(urlaub.getTag()) == 0) {
                klausurSelberTag.add(tmpKlausur);
            }
        }
        if(!klausurSelberTag.isEmpty()) {
            //eine Klausur ist am selben Tag: Urlaub darf frei eingeteilt werden
            //überschneidende Urlaubszeit erstatten
            //TODO: mehr als eine Klausur an einem Tag ? vllt forschleife mit size der klausurSelberTag 
            if(klausurSelberTag.size() == 1) {
                if(klausurSelberTag.stream().toList().get(0).isPraesenz()) {
                    if(klausurSelberTag.stream().toList().get(0).getBeginn().isBefore(LocalTime.of(10,30))){ //vor 10.30 beginnt
                        if(klausurSelberTag.stream().toList().get(0).getEnd().isAfter(LocalTime.of(10,30))){ //nach 10.30 aufhört
                        zuErstattenderUrlaub = new Urlaub(klausurSelberTag.stream().toList().get(0).getDate().toString(), "08,30","12,30");
                        }
                        else{
                            zuErstattenderUrlaub = new Urlaub(klausurSelberTag.stream().toList().get(0).getDate().toString(), "08,30",klausurSelberTag.stream().toList().get(0).getEnd().plusHours(2).toString());
                        }
                    }
                    else{ // nach 10.30 Beginn
                        zuErstattenderUrlaub = new Urlaub(klausurSelberTag.stream().toList().get(0).getDate().toString(), klausurSelberTag.stream().toList().get(0).getBeginn().minusHours(2).toString(), "12:30");
                    }
                }
                else{ // nicht in Präsenz
                    if(klausurSelberTag.stream().toList().get(0).getBeginn().isBefore(LocalTime.of(9,0))){ //vor 9.00 beginnt
                        if(klausurSelberTag.stream().toList().get(0).getEnd().isAfter(LocalTime.of(12,0))){ //nach 12.00 aufhört
                            zuErstattenderUrlaub = new Urlaub(klausurSelberTag.stream().toList().get(0).getDate().toString(), "08,30","12,30");
                        }
                        else{
                            zuErstattenderUrlaub = new Urlaub(klausurSelberTag.stream().toList().get(0).getDate().toString(), "08,30",klausurSelberTag.stream().toList().get(0).getEnd().plusMinutes(30).toString());
                        }
                    }
                    else{ // nach 9 Beginn
                        if(klausurSelberTag.stream().toList().get(0).getEnd().isAfter(LocalTime.of(12,0))){ //nach 12.00 aufhört
                            zuErstattenderUrlaub = new Urlaub(klausurSelberTag.stream().toList().get(0).getDate().toString(), klausurSelberTag.stream().toList().get(0).getBeginn().minusMinutes(30).toString(),"12,30");
                        }
                        else{
                            zuErstattenderUrlaub = new Urlaub(klausurSelberTag.stream().toList().get(0).getDate().toString(), klausurSelberTag.stream().toList().get(0).getBeginn().minusMinutes(30).toString(),klausurSelberTag.stream().toList().get(0).getEnd().plusMinutes(30).toString());
                        }

                    }

                }
            }

        }
        else { //keine Klausur am selben Tag
            if(urlaubeSelberTag.isEmpty()) {
                //der anzulegende Urlaub ist der erste Urlaubsblock an dem Tag: den ganzen Tag frei oder max 2,5 Stunden
                if(Duration.between(urlaub.getVon(),urlaub.getBis()).toMinutes() > 150 && Duration.between(urlaub.getVon(),urlaub.getBis()).toMinutes() < 240 ){
                    throw new Exception("Der Urlaub ist weder den ganzen Tag lang noch weniger als 2.5 Stunden lang");
                }
            }
            else if(urlaubeSelberTag.size() == 1) {
                //schon 1 Urlaub am selben Tag: Blöcke müssen am Anfang und Ende liegen mit mind. 90 Min Arbeitszeit dazwischen
                if(urlaubeSelberTag.stream().toList().get(0).getVon().compareTo(LocalTime.of(8,30)) == 0){
                    if(Duration.between(urlaubeSelberTag.stream().toList().get(0).getBis(), urlaub.getVon()).toMinutes() < 90) { //min. 90 arbeiten
                        throw new Exception("Man muss mindestens 90 Minuten zwischen den beiden Urlaubsblöcken arbeiten");
                    }
                     if(!(urlaub.getBis().compareTo(LocalTime.of(12,30)) == 0)){
                        throw new Exception("Der neue Urlaub muss am Ende des Tages liegen");
                    }
                }
                else if(urlaubeSelberTag.stream().toList().get(0).getBis().compareTo(LocalTime.of(12,30)) == 0){
                    if(Duration.between( urlaub.getBis(),urlaubeSelberTag.stream().toList().get(0).getVon()).toMinutes() < 90) { //min. 90 arbeiten
                        throw new Exception("Man muss mindestens 90 Minuten zwischen den beiden Urlaubsblöcken arbeiten");
                    }
                    if(!(urlaub.getVon().compareTo(LocalTime.of(8,30)) == 0)){
                        throw new Exception("Der neue Urlaub muss am Anfang des Tages liegen");
                    }
                }
                else{
                    throw new Exception("Der bereits gebuchte Urlaub ist weder am Anfang noch am Ende des Tages");
                }
            }
            else if(urlaubeSelberTag.size() == 2) {
                //schon 2 Urlaube am selben Tag: kein weiterer Urlaub kann gebucht werden
                throw new Exception("Es wurden bereits zwei Urlaubsblöcke genommen");
            }
        }
        return zuErstattenderUrlaub;
    }

    public void validiereKlausurAnmeldung(Klausur klausur) throws Exception {
        //Die klausur ist mindestens einen Tag später
        if(!klausur.getDate().isAfter(LocalDate.now())) {
            throw new Exception("Klausur findet heute statt. Anmeldung nicht mehr moeglich");
        }
        //Überprüft, ob der Student schon angemeldet ist
        for(Klausur tmpKlausur : this.klausuren) {
            if(tmpKlausur.getLsfid() == klausur.getLsfid()) {
                throw new Exception("Du bist bereits bei der Klausur angemeldet");
            }
        }

    }

    public void addUrlaub(Urlaub urlaub) {
        urlaube.add(urlaub);
    }
}
