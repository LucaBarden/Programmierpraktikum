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
    private static String ENDE_PRAKTIKUM   = "12:30";

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



    public Set<Urlaub> validiereUrlaub(Student student, Urlaub urlaub, Set<KlausurRef> klausuren) throws Exception {
        Set<Urlaub> urlaube = student.getUrlaube();
        Set<Urlaub> zuErstattendeZeiten = new HashSet<>();

        student.checkAufGrundregeln(urlaub, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);

        Set<Urlaub> urlaubeSelberTag = urlaubSelberTag(urlaub, urlaube);
        Set<KlausurRef> klausurSelberTag = klausurSelberTag(urlaub, klausuren);

        //eine oder mehrere Klausuren am selben Tag: Urlaub darf frei eingeteilt werden
        //& überschneidende Urlaubszeit wird erstattet (keine Exceptions)
        if (!klausurSelberTag.isEmpty()) {
            klausurenAmSelbenTag(zuErstattendeZeiten, klausurSelberTag);
            gleichzeitigerUrlaub(zuErstattendeZeiten, urlaubeSelberTag, urlaub);
        //keine Klausur am selben Tag
        } else {
            keineKlausurSelberTag(urlaub, urlaubeSelberTag);
        }
        return berechneGueltigeUrlaube(urlaub, zuErstattendeZeiten);
    }

    private Set<KlausurRef> klausurSelberTag(Urlaub urlaub, Set<KlausurRef> klausuren) {
        Set<KlausurRef> kSelberTag = new HashSet<>();
        for (KlausurRef tmpKlausur : klausuren) {
            if (tmpKlausur.getTag().compareTo(urlaub.getTag()) == 0) {
                kSelberTag.add(tmpKlausur);
            }
        }
        return kSelberTag;
    }

    private Set<Urlaub> urlaubSelberTag(Urlaub urlaub, Set<Urlaub> urlaube) {
        Set<Urlaub> uSelberTag = new HashSet<>();
        for (Urlaub tmpUrlaub : urlaube) {
            if (tmpUrlaub.getTag().compareTo(urlaub.getTag()) == 0) {
                uSelberTag.add(tmpUrlaub);
            }
        }
        return uSelberTag;
    }

    private void klausurenAmSelbenTag(Set<Urlaub> zuErstattenderUrlaube, Set<KlausurRef> klausurSelberTag) {
        for(KlausurRef k : klausurSelberTag) {
            String klausurTag            = k.getTag().toString();
            LocalTime anfangsZeitKlausur = k.getVon();
            LocalTime endZeitKlausur     = k.getBis();

            if(k.isPraesenz()) {
                //puffer ist die Zeit in Minuten, die man vor und nach einer Klausur freigestellt wird
                int puffer = 120; //bei einer Präsenzklausur wird man je 2 Stunden = 120 Min. vorher und nachher freigestellt
                erstattungsZeiten(zuErstattenderUrlaube, klausurTag, anfangsZeitKlausur, endZeitKlausur, puffer);
            } else { // nicht in Präsenz
                int puffer = 30; //bei einer Onlineklausur wird man je 30 Min vorher und nachher freigestellt
                erstattungsZeiten(zuErstattenderUrlaube, klausurTag, anfangsZeitKlausur, endZeitKlausur, puffer);
            }
        }
    }

    private void erstattungsZeiten(Set<Urlaub> zuErstattendeZeiten, String klausurTag, LocalTime anfangsZeitDerKlausur, LocalTime endZeitDerKlausur, int puffer) {
        //die Klausur fängt vor (Praktikumsbeginn + puffer) an: Urlaub wird ab Praktikumsbeginn erstattet
        if (anfangsZeitDerKlausur.isBefore(LocalTime.parse(BEGINN_PRAKTIKUM).plusMinutes(puffer))) {
            //die Klausur endet nach (Praktikumsende - puffer): Urlaub wird bis Praktikumsende erstattet
            if (endZeitDerKlausur.isAfter(LocalTime.parse(ENDE_PRAKTIKUM).minusMinutes(puffer))) {
                zuErstattendeZeiten.add(new Urlaub(klausurTag, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM));
            //die Klausur endet vor (Praktikumsende - puffer): Urlaub wird ab (Klausurende + puffer) erstattet
            } else {
                zuErstattendeZeiten.add(new Urlaub(klausurTag, BEGINN_PRAKTIKUM, endZeitDerKlausur.plusMinutes(30).toString()));
            }

        //die Klausur fängt nach (Praktikumsbeginn + puffer) an: Urlaub wird ab (Klausurbeginn - puffer) erstattet
        } else { // nach 9 Beginn
            //die Klausur endet nach (Praktikumsende - puffer): Urlaub wird bis Praktikumsende erstattet
            if (endZeitDerKlausur.isAfter(LocalTime.parse(ENDE_PRAKTIKUM).minusMinutes(puffer))) {
                zuErstattendeZeiten.add(new Urlaub(klausurTag, anfangsZeitDerKlausur.minusMinutes(puffer).toString(), ENDE_PRAKTIKUM));
            //die Klausur endet vor (Praktikumsende - puffer): Urlaub wird ab (Klausurende + puffer) erstattet
            } else {
                zuErstattendeZeiten.add(new Urlaub(klausurTag, anfangsZeitDerKlausur.minusMinutes(puffer).toString(), endZeitDerKlausur.plusMinutes(puffer).toString()));
            }
        }
    }

    private void gleichzeitigerUrlaub(Set<Urlaub> zuErstattendeZeiten, Set<Urlaub> urlaubeSelberTag, Urlaub urlaub) {
        //TODO: Überschneidungen bei altem und neuem Urlaub erstatten (analog zur Erstattung wegen Klausur)
        /*for(Urlaub u : urlaubeSelberTag) {
            String urlaubTag            = u.getTag().toString();
            LocalTime anfangsZeitUrlaub = u.getVon();
            LocalTime endZeitUrlaub     = u.getBis();

            //erstattungsZeitenUrlaub(zuErstattendeZeiten, urlaubTag, anfangsZeitUrlaub, endZeitUrlaub);
            if("alter und neuer Urlaub überschneiden sich oder sind direkt hintereinander") {
                //Startzeit des alten Urlaubs liegt vor Startzeit des neuen Urlaubs und Endzeit des neuen Urlaubs liegt nach Endzeit des alten Urlaubs
                //Startzeit alt & Endzeit neu
                if() {
                }
                //Startzeit alt & Endzeit alt
                else if() {

                }
                //Startzeit neu & Endzeit alt
                else if() {

                }
                //Startzeit neu & Endzeit neu
                else if() {

                }
            }
        }*/
    }

    private void keineKlausurSelberTag(Urlaub urlaub, Set<Urlaub> urlaubeSelberTag) throws Exception {
        //der anzulegende Urlaub ist der erste Urlaubsblock an dem Tag
        if (urlaubeSelberTag.isEmpty()) {
            //den ganzen Tag frei oder max 2,5 Stunden sind erlaubt
            if (Duration.between(urlaub.getVon(), urlaub.getBis()).toMinutes() > 150 && Duration.between(urlaub.getVon(), urlaub.getBis()).toMinutes() < 240) {
                throw new Exception("Der Urlaub ist weder den ganzen Tag lang noch weniger als 2.5 Stunden lang");
            }
            //schon 1 Urlaub am selben Tag
        } else if (urlaubeSelberTag.size() == 1) {
            //die zwei Urlaubsblöcke müssen am Anfang und Ende liegen mit mind. 90 Min Arbeitszeit dazwischen
            regelnFuerZweiBloecke(urlaub, urlaubeSelberTag);
            //schon mehr als ein Urlaub am selben Tag
        } else if (urlaubeSelberTag.size() > 1) {
            //kein weiterer Urlaub kann gebucht werden
            throw new Exception("Es wurden bereits zwei Urlaubsblöcke genommen");
        }
    }

    private void regelnFuerZweiBloecke(Urlaub urlaub, Set<Urlaub> urlaubeSelberTag) throws Exception {
        Urlaub amSelbenTag = urlaubeSelberTag.stream().toList().get(0);
        //der zuvor gebuchte Urlaub liegt am Anfang der Praktikumszeit
        if (amSelbenTag.getVon().compareTo(LocalTime.parse(BEGINN_PRAKTIKUM)) == 0) {
            if (Duration.between(amSelbenTag.getBis(), urlaub.getVon()).toMinutes() < 90) {
                throw new Exception("Man muss mindestens 90 Minuten zwischen den beiden Urlaubsblöcken arbeiten");
            }
            if (!(urlaub.getBis().compareTo(LocalTime.parse(ENDE_PRAKTIKUM)) == 0)) {
                throw new Exception("Der neue Urlaub muss am Ende des Tages liegen");
            }
        //der zuvor gebuchte Urlaub liegt am Ende der Praktikumszeit
        } else if (amSelbenTag.getBis().compareTo(LocalTime.parse(ENDE_PRAKTIKUM)) == 0) {
            if (Duration.between(urlaub.getBis(), amSelbenTag.getVon()).toMinutes() < 90) { //min. 90 arbeiten
                throw new Exception("Man muss mindestens 90 Minuten zwischen den beiden Urlaubsblöcken arbeiten");
            }
            if (!(urlaub.getVon().compareTo(LocalTime.parse(BEGINN_PRAKTIKUM)) == 0)) {
                throw new Exception("Der neue Urlaub muss am Anfang des Tages liegen");
            }
            //der zuvor gebuchte Urlaub ist weder am Anfang noch am Ende des Tages
        } else {
            throw new Exception("Der bereits gebuchte Urlaub ist weder am Anfang noch am Ende des Tages");
        }
    }

    private Set<Urlaub> berechneGueltigeUrlaube(Urlaub urlaub, Set<Urlaub> zuErstattenderUrlaube) throws InterruptedException {
        Set<Urlaub> zuPruefendeUrlaube = new HashSet<>();
        Set<Urlaub> stashUrlaube = new HashSet<>();
        zuPruefendeUrlaube.add(urlaub);
        boolean aenderung = true;

        while(aenderung) {
            aenderung = false;
            for (Urlaub pruefen : zuPruefendeUrlaube) {
                if(aenderung) break;
                for (Urlaub u : zuErstattenderUrlaube) {
                    //Startzeit von u ist nach urlaub oder gleichzeitig mit dessen Endzeit
                    if (u.getVon().isAfter(pruefen.getBis()) || u.getVon().equals(pruefen.getBis())) {
                        //nichts erstatten: der zu buchende Urlaub ist gültig
                        stashUrlaube.add(pruefen);
                    }
                    //Startzeit von u liegt im neu zu buchenden Urlaub
                    else if ((u.getVon().isAfter(pruefen.getVon())) && u.getVon().isBefore(pruefen.getBis())) {
                        //Endzeit von u liegt im neu zu buchenden Urlaub
                        if (u.getBis().isBefore(pruefen.getBis())) {
                            //Mitte erstatten: der geplante Urlaub urlaub wird in zwei Urlaubsblöcke aufgeteilt
                            Urlaub ersterBlock = new Urlaub(pruefen.getTag().toString(), pruefen.getVon().toString(), u.getVon().toString());
                            Urlaub zweiterBlock = new Urlaub(pruefen.getTag().toString(), u.getBis().toString(), pruefen.getBis().toString());
                            stashUrlaube.remove(pruefen);
                            stashUrlaube.add(ersterBlock);
                            stashUrlaube.add(zweiterBlock);
                            aenderung = true;
                            break;
                        }
                        //Endzeit von u ist nach urlaub oder gleichzeitig mit dessen Endzeit
                        else if (u.getBis().isAfter(pruefen.getBis()) || u.getBis().equals(pruefen.getBis())) {
                            //Ende erstatten
                            Urlaub stashUrlaub = new Urlaub(pruefen.getTag().toString(), pruefen.getVon().toString(), u.getVon().toString());
                            stashUrlaube.add(stashUrlaub);
                            /* pruefen.setBis(u.getVon());*/
                            aenderung = true;
                            break;
                        }
                    }
                    //Startzeit von u ist vor urlaub oder gleichzeitig mit der Startzeit von urlaub
                    else if (u.getVon().isBefore(pruefen.getVon()) || u.getVon().equals(pruefen.getVon())) {
                        //Endzeit von u liegt in urlaub
                        if (u.getBis().isAfter(pruefen.getVon()) && u.getBis().isBefore(pruefen.getBis())) {
                            //anfang erstatten
                            Urlaub stashUrlaub = new Urlaub(pruefen.getTag().toString(), u.getBis().toString(), pruefen.getBis().toString());
                            stashUrlaube.add(stashUrlaub);
                            //pruefen.setVon(u.getBis());
                            aenderung = true;
                            break;
                        }
                        //Endzeit von u ist vor urlaub oder gleichzeitig mit dessen Startzeit
                        else if (u.getBis().isBefore(pruefen.getVon()) || u.getBis().equals(pruefen.getVon())) {
                            //nichts erstatten
                            stashUrlaube.add(pruefen);
                        }
                    }
                }
            }
            zuPruefendeUrlaube = stashUrlaube;
            stashUrlaube = new HashSet<>();
        }
        return zuPruefendeUrlaube;
    }
}
