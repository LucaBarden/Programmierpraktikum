package de.propra.chicken.domain.service;

import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.KlausurRef;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class StudentService {

    private static String BEGINN_PRAKTIKUM = "08:30";
    private static String ENDE_PRAKTIKUM = "12:30";

    public Set<Urlaub> validiereUrlaub(Student student, Urlaub urlaub, Set<KlausurRef> klausuren) throws Exception {
        //TODO validiere Urlaub
        Set<Urlaub> urlaubeSelberTag = new HashSet<>();
        Set<KlausurRef> klausurSelberTag = new HashSet<>();
        Set<Urlaub> zuErstattenderUrlaube = new HashSet<>();
        Set<Urlaub> urlaube = student.getUrlaube();

        student.checkAufGrundregeln(urlaub);

        urlaubSelberTag(urlaub, urlaubeSelberTag, urlaube);
        klausurSelberTag(urlaub, klausuren, klausurSelberTag);
        if (!klausurSelberTag.isEmpty()) {
            //eine Klausur ist am selben Tag: Urlaub darf frei eingeteilt werden
            //überschneidende Urlaubszeit erstatten
            //TODO: mehr als eine Klausur an einem Tag ? vllt forschleife mit size der klausurSelberTag
            for(KlausurRef k : klausurSelberTag) {
                String klausurTag = k.getTag().toString();
                LocalTime anfangsZeitDerKlausur = k.getVon();
                LocalTime endZeitDerKlausur = k.getBis();

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

        return berechneGueltigeUrlaube(urlaub, zuErstattenderUrlaube);

    }

    private void klausurSelberTag(Urlaub urlaub, Set<KlausurRef> klausuren, Set<KlausurRef> klausurSelberTag) {
        for (KlausurRef tmpKlausur : klausuren) {
            if (tmpKlausur.getTag().compareTo(urlaub.getTag()) == 0) {
                klausurSelberTag.add(tmpKlausur);
            }
        }
    }

    private void urlaubSelberTag(Urlaub urlaub, Set<Urlaub> urlaubeSelberTag, Set<Urlaub> urlaube) {
        for (Urlaub tmpUrlaub : urlaube) {
            if (tmpUrlaub.getTag().compareTo(urlaub.getTag()) == 0) {
                urlaubeSelberTag.add(tmpUrlaub);
            }
        }
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

    public Set<Urlaub> validiereKlausurAnmeldung(Klausur klausur, Set<Klausur> angemeldeteKlausuren, Set<Urlaub> urlaube) throws Exception {
        //TODO: urlaube erstatten (wenn überlappt)
        //Die klausur ist mindestens einen Tag später
        if (!klausur.getDate().isAfter(LocalDate.now())) {
            throw new Exception("Klausur findet heute statt. Anmeldung nicht mehr moeglich");
        }
        //Überprüft, ob der Student schon angemeldet ist
        for (Klausur tmpKlausur : angemeldeteKlausuren) {
            if (tmpKlausur.getLsfid() == klausur.getLsfid()) {
                throw new Exception("Du bist bereits bei der Klausur angemeldet");
            }
        }

        return null;
    }

    public Student erstatteUrlaube(Set<Urlaub> zuErstattendeUrlaube) {
        //TODO erstatte Urlaube
        return null;
    }
}
