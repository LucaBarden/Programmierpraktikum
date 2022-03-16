package de.propra.chicken.domain.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class Student {

    private final long studentID;
    private int resturlaub;

    private static String BEGINN_PRAKTIKUM = "08:30";
    private static String ENDE_PRAKTIKUM = "12:30";

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

    public Set<Urlaub> validiereUrlaub(Urlaub urlaub) throws Exception {
        //TODO validiere Urlaub
        /*Set<Urlaub> urlaubeSelberTag = new HashSet<>();
        Set<Klausur> klausurSelberTag = new HashSet<>();
        Set<Urlaub> zuErstattenderUrlaube = new HashSet<>();

        checkAufGrundregeln(urlaub);

        for (Urlaub tmpUrlaub : urlaube) {
            if (tmpUrlaub.getTag().compareTo(urlaub.getTag()) == 0) {
                urlaubeSelberTag.add(tmpUrlaub);
            }
        }
        for (Klausur tmpKlausur : klausuren) {
            if (tmpKlausur.getDate().compareTo(urlaub.getTag()) == 0) {
                klausurSelberTag.add(tmpKlausur);
            }
        }
        if (!klausurSelberTag.isEmpty()) {
            //eine Klausur ist am selben Tag: Urlaub darf frei eingeteilt werden
            //überschneidende Urlaubszeit erstatten
            //TODO: mehr als eine Klausur an einem Tag ? vllt forschleife mit size der klausurSelberTag
            for(Klausur k : klausurSelberTag) {
                String klausurTag = k.getDate().toString();
                LocalTime anfangsZeitDerKlausur = k.getBeginn();
                LocalTime endZeitDerKlausur = k.getEnd();

                if (k.isPraesenz()) {
                    if (anfangsZeitDerKlausur.isBefore(LocalTime.parse(BEGINN_PRAKTIKUM).plusHours(2))) { //vor 10.30 beginnt
                        if (endZeitDerKlausur.isAfter(LocalTime.parse(ENDE_PRAKTIKUM).minusHours(2))) { //nach 10.30 aufhört
                            zuErstattenderUrlaube.add(new Urlaub(klausurTag, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
                        } else {
                            zuErstattenderUrlaube.add(new Urlaub(klausurTag, BEGINN_PRAKTIKUM, endZeitDerKlausur.plusHours(2).toString()));
                        }
                    } else { // nach 10.30 Beginn
                        zuErstattenderUrlaube.add(new Urlaub(klausurTag, anfangsZeitDerKlausur.minusHours(2).toString(), ENDE_PRAKTIKUM));
                    }
                } else { // nicht in Präsenz
                    if (anfangsZeitDerKlausur.isBefore(LocalTime.parse(BEGINN_PRAKTIKUM).plusMinutes(30))) { //vor 9.00 beginnt
                        if (endZeitDerKlausur.isAfter(LocalTime.parse(ENDE_PRAKTIKUM).minusMinutes(30))) { //nach 12.00 aufhört
                            zuErstattenderUrlaube.add(new Urlaub(klausurTag, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
                        } else {
                            zuErstattenderUrlaube.add(new Urlaub(klausurTag, BEGINN_PRAKTIKUM, endZeitDerKlausur.plusMinutes(30).toString()));
                        }
                    } else { // nach 9 Beginn
                        if (endZeitDerKlausur.isAfter(LocalTime.parse(ENDE_PRAKTIKUM).minusMinutes(30))) { //nach 12.00 aufhört
                            zuErstattenderUrlaube.add(new Urlaub(klausurTag, anfangsZeitDerKlausur.minusMinutes(30).toString(), ENDE_PRAKTIKUM));
                        } else {
                            zuErstattenderUrlaube.add(new Urlaub(klausurTag, anfangsZeitDerKlausur.minusMinutes(30).toString(), endZeitDerKlausur.plusMinutes(30).toString()));
                        }

                    }
                }

            }

        } else { //keine Klausur am selben Tag
            if (urlaubeSelberTag.isEmpty()) {
                //der anzulegende Urlaub ist der erste Urlaubsblock an dem Tag: den ganzen Tag frei oder max 2,5 Stunden
                if (Duration.between(urlaub.getVon(), urlaub.getBis()).toMinutes() > 150 && Duration.between(urlaub.getVon(), urlaub.getBis()).toMinutes() < 240) {
                    throw new Exception("Der Urlaub ist weder den ganzen Tag lang noch weniger als 2.5 Stunden lang");
                }
            } else if (urlaubeSelberTag.size() == 1) {
                //schon 1 Urlaub am selben Tag: Blöcke müssen am Anfang und Ende liegen mit mind. 90 Min Arbeitszeit dazwischen
                checkTwoBlockConditions(urlaub, urlaubeSelberTag);
            } else if (urlaubeSelberTag.size() == 2) {
                //schon 2 Urlaube am selben Tag: kein weiterer Urlaub kann gebucht werden
                throw new Exception("Es wurden bereits zwei Urlaubsblöcke genommen");
            }
        }

        return berechneGueltigeUrlaube(urlaub, zuErstattenderUrlaube);*/
        return null;
    }

    private Set<Urlaub> berechneGueltigeUrlaube(Urlaub urlaub, Set<Urlaub> zuErstattenderUrlaube) {
        Set<Urlaub> gueltigeUrlaube = new HashSet<>();
        for(Urlaub u : zuErstattenderUrlaube) {
            //startzeit von u ist nach urlaub
            if(u.getVon().isAfter(urlaub.getBis()) || u.getVon().equals(urlaub.getBis())) {
                //nichts erstatten
            }
            //startzeit von u ist zwischen urlaub
            else if((u.getVon().isAfter(urlaub.getVon()) || u.getVon().equals(urlaub.getVon())) && u.getVon().isBefore(urlaub.getBis())) {
                //endezeit von u ist zwischen urlaub
                if(u.getBis().isBefore(urlaub.getBis())) {
                     //mitte erstatten
                }
                //endezeit von u ist nach urlaub
                else {
                    //ende erstatten
                }
            }
            //startzeit von u ist vor urlaub
            else {
                    //endzeit von u ist zwischen Urlaub //anfang erstatten
                    //endzeit von u ist nach urlaub //alles erstatten
                    //endzeit von u ist vor urlaub //nichts erstatten
            }


        }
        return gueltigeUrlaube;
    }

    private void checkTwoBlockConditions(Urlaub urlaub, Set<Urlaub> urlaubeSelberTag) throws Exception {
        if (urlaubeSelberTag.stream().toList().get(0).getVon().compareTo(LocalTime.parse(BEGINN_PRAKTIKUM)) == 0) {
            if (Duration.between(urlaubeSelberTag.stream().toList().get(0).getBis(), urlaub.getVon()).toMinutes() < 90) { //min. 90 arbeiten
                throw new Exception("Man muss mindestens 90 Minuten zwischen den beiden Urlaubsblöcken arbeiten");
            }
            if (!(urlaub.getBis().compareTo(LocalTime.parse(ENDE_PRAKTIKUM)) == 0)) {
                throw new Exception("Der neue Urlaub muss am Ende des Tages liegen");
            }
        } else if (urlaubeSelberTag.stream().toList().get(0).getBis().compareTo(LocalTime.parse(ENDE_PRAKTIKUM)) == 0) {
            if (Duration.between(urlaub.getBis(), urlaubeSelberTag.stream().toList().get(0).getVon()).toMinutes() < 90) { //min. 90 arbeiten
                throw new Exception("Man muss mindestens 90 Minuten zwischen den beiden Urlaubsblöcken arbeiten");
            }
            if (!(urlaub.getVon().compareTo(LocalTime.parse(BEGINN_PRAKTIKUM)) == 0)) {
                throw new Exception("Der neue Urlaub muss am Anfang des Tages liegen");
            }
        } else {
            throw new Exception("Der bereits gebuchte Urlaub ist weder am Anfang noch am Ende des Tages");
        }
    }

    private void checkAufGrundregeln(Urlaub urlaub) throws Exception {
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

    public void validiereKlausurAnmeldung(Klausur klausur) throws Exception {
        //Die klausur ist mindestens einen Tag später
        if (!klausur.getDate().isAfter(LocalDate.now())) {
            throw new Exception("Klausur findet heute statt. Anmeldung nicht mehr moeglich");
        }
        //Überprüft, ob der Student schon angemeldet ist
        for (Klausur tmpKlausur : this.klausuren) {
            if (tmpKlausur.getLsfid() == klausur.getLsfid()) {
                throw new Exception("Du bist bereits bei der Klausur angemeldet");
            }
        }

    }

    public void addUrlaub(Urlaub urlaub) {
        urlaube.add(urlaub);
    }


}
