package de.propra.chicken.domain.service;

import de.propra.chicken.domain.model.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
@Service
public class StudentService {

    private static String BEGINN_PRAKTIKUM = "08:30";
    private static String ENDE_PRAKTIKUM   = "12:30";

    public Set<Urlaub> validiereKlausurAnmeldung(KlausurRef anzumeldendeKlausur, KlausurData klausurData, Set<KlausurData> angemeldeteKlausuren, Set<Urlaub> urlaube, Set<KlausurRef> angemeldeteKlausurenRefs) throws Exception {
        //TODO: urlaube erstatten (wenn überlappt)
        //Die klausur ist mindestens einen Tag später
        if (!klausurData.tag().isAfter(LocalDate.now())) {
            throw new Exception("Klausur findet heute statt. Anmeldung nicht mehr moeglich");
        }
        //Überprüft, ob der Student schon angemeldet ist
        for (KlausurRef tmpKlausur : angemeldeteKlausurenRefs) {
            if (tmpKlausur.getLsfID() == anzumeldendeKlausur.getLsfID()) {
                throw new Exception("Du bist bereits bei der Klausur angemeldet");
            }
        }

        return null;
    }

    public Student erstatteUrlaube(Set<Urlaub> zuErstattendeUrlaube) {
        //TODO erstatte Urlaube
        return null;
    }



    public Set<Urlaub> validiereUrlaub(Student student, Urlaub urlaub, Set<KlausurData> klausuren) throws Exception {
        Set<Urlaub> urlaube = student.getUrlaube();
        Set<Urlaub> zuErstattendeZeiten = new HashSet<>();

        student.checkAufGrundregeln(urlaub, BEGINN_PRAKTIKUM, ENDE_PRAKTIKUM);

        Set<Urlaub> urlaubeSelberTag = urlaubSelberTag(urlaub, urlaube);
        Set<KlausurData> klausurSelberTag = klausurSelberTag(urlaub, klausuren);
        Set<Urlaub> gueltigeUrlaube = new HashSet<>();

        //eine oder mehrere Klausuren am selben Tag: Urlaub darf frei eingeteilt werden
        //& überschneidende Urlaubszeit wird erstattet (keine Exceptions)
        if (!klausurSelberTag.isEmpty()) {
            /* 1) wenn mind. eine Klausur am selben Tag ist, wird die dafür zu erstattende Zeit berechnet
             *    (Klausuranfang - puffer) bis (Klausurende + puffer) wird erstattet
             * 2) die zu erstattende Zeit wird von dem zu buchenden Urlaub abgezogen und der Urlaub ggf in zwei oder
             *    mehr Blöcke aufgeteilt
             * 3) der bisher berechnete gültige Urlaub wird auf Überschneidungen mit zuvor genommenem Urlaub überprüft
             *    und überschneidende Urlaubsblöcke werden zu einem Block zusammen gelegt bzw nur der längere behalten
             */
            klausurenAmSelbenTag(zuErstattendeZeiten, klausurSelberTag); // 1)
            gueltigeUrlaube = berechneGueltigeUrlaube(urlaub, zuErstattendeZeiten); // 2)
            gueltigeUrlaube = gleichzeitigerUrlaub(urlaubeSelberTag, gueltigeUrlaube); // 3)
        //keine Klausur am selben Tag
        } else {
            keineKlausurSelberTag(urlaub, urlaubeSelberTag);
        }
        return berechneGueltigeUrlaube(urlaub, zuErstattendeZeiten);
    }

    private Set<KlausurData> klausurSelberTag(Urlaub urlaub, Set<KlausurData> klausuren) {
        Set<KlausurData> kSelberTag = new HashSet<>();
        for (KlausurData tmpKlausur : klausuren) {
            if (tmpKlausur.tag().compareTo(urlaub.getTag()) == 0) {
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

    private void klausurenAmSelbenTag(Set<Urlaub> zuErstattenderUrlaube, Set<KlausurData> klausurSelberTag) {
        for(KlausurData k : klausurSelberTag) {
            String klausurTag            = k.tag().toString();
            LocalTime anfangsZeitKlausur = k.von();
            LocalTime endZeitKlausur     = k.bis();

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
                zuErstattendeZeiten.add(new Urlaub(klausurTag, BEGINN_PRAKTIKUM, endZeitDerKlausur.plusMinutes(puffer).toString()));
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

    private Set<Urlaub> gleichzeitigerUrlaub(Set<Urlaub> urlaubeSelberTag, Set<Urlaub> neueUrlaube) {
        //TODO: Testen im StudentService (mind. eine Klausur an dem Tag + neuer Urlaub überschneidet sich mit altem Urlaub)

        if(urlaubeSelberTag.size() == 0) return neueUrlaube;
        if(neueUrlaube.size() == 0) return urlaubeSelberTag;

        Set<Urlaub> zuPruefendeUrlaube = urlaubeSelberTag;
        Set<Urlaub> gueltigeUrlaube = new HashSet<>();

        for(Urlaub neu : neueUrlaube) {
            for (Urlaub alt : zuPruefendeUrlaube) {
                if (alt.getBeginn().equals(neu.getBeginn())) {
                    if (alt.getEnd().equals(neu.getEnd())) {
                        //1: beide Urlaube sind gleich, einer wird übernommen
                        gueltigeUrlaube.add(neu);
                    } else if (alt.getEnd().isAfter(neu.getEnd())) {
                        //2: der alte Urlaub wird übernommen
                        gueltigeUrlaube.add(alt);
                    } else if (alt.getEnd().isBefore(neu.getEnd())) {
                        //3: der neue Urlaub wird übernommen
                        gueltigeUrlaube.add(neu);
                    }
                } else if (alt.getBeginn().isAfter(neu.getBeginn()) && alt.getBeginn().isBefore(neu.getEnd())) {
                    if (alt.getEnd().equals(neu.getEnd())) {
                        //4: der neue Urlaub wird übernommen
                        gueltigeUrlaube.add(neu);
                    } else if (alt.getEnd().isAfter(neu.getEnd())) {
                        //5: Start vom neuen und Ende vom alten Urlaub
                        gueltigeUrlaube.add(new Urlaub(neu.getTag().toString(), neu.getBeginn().toString(), alt.getEnd().toString()));
                    } else if (alt.getEnd().isBefore(neu.getEnd())) {
                        //6: der neue Urlaub wird übernommen
                        gueltigeUrlaube.add(neu);
                    }
                } else if (alt.getBeginn().isBefore(neu.getBeginn()) && (neu.getBeginn().isBefore(alt.getEnd()) || neu.getBeginn().equals(alt.getEnd()))) {
                    if (alt.getEnd().equals(neu.getEnd())) {
                        //7: der alte Urlaub wird übernommen
                        gueltigeUrlaube.add(alt);
                    } else if (alt.getEnd().isAfter(neu.getEnd())) {
                        //8: der alte Urlaub wird übernommen
                        gueltigeUrlaube.add(alt);
                    } else if (alt.getEnd().isBefore(neu.getEnd())) {
                        //9: Start vom alten und Ende vom neuen Urlaub
                        gueltigeUrlaube.add(new Urlaub(neu.getTag().toString(), alt.getBeginn().toString(), neu.getEnd().toString()));
                    }
                }
                //die alten und der neue Urlaub überschneiden sich nicht: beide sind gültig
                else {
                    gueltigeUrlaube.add(alt);
                    gueltigeUrlaube.add(neu);
                }
            }
            zuPruefendeUrlaube = gueltigeUrlaube;
            gueltigeUrlaube.removeAll(gueltigeUrlaube);
        }
        return zuPruefendeUrlaube;
    }

    private void keineKlausurSelberTag(Urlaub urlaub, Set<Urlaub> urlaubeSelberTag) throws Exception {
        //der anzulegende Urlaub ist der erste Urlaubsblock an dem Tag
        if (urlaubeSelberTag.isEmpty()) {
            //den ganzen Tag frei oder max 2,5 Stunden sind erlaubt
            if (Duration.between(urlaub.getBeginn(), urlaub.getEnd()).toMinutes() > 150 && Duration.between(urlaub.getBeginn(), urlaub.getEnd()).toMinutes() < 240) {
                throw new Exception("Der Urlaub ist weder den ganzen Tag lang noch weniger als 2.5 Stunden lang");
            }
            //schon 1 Urlaub am selben Tag
        } else if (urlaubeSelberTag.size() == 1) {
            //die zwei Urlaubsblöcke müssen am Anfang und Ende liegen mit mind. 90 Min Arbeitszeit dazwischen
            regelnFuerZweiBloecke(urlaub, urlaubeSelberTag);
            //schon mehr als ein Urlaub am selben Tag
        } else{
            //kein weiterer Urlaub kann gebucht werden
            throw new Exception("Es wurden bereits zwei Urlaubsblöcke genommen");
        }
    }

    private void regelnFuerZweiBloecke(Urlaub urlaub, Set<Urlaub> urlaubeSelberTag) throws Exception {
        Urlaub amSelbenTag = urlaubeSelberTag.stream().toList().get(0);
        //der zuvor gebuchte Urlaub liegt am Anfang der Praktikumszeit
        if (amSelbenTag.getBeginn().compareTo(LocalTime.parse(BEGINN_PRAKTIKUM)) == 0) {
            if (Duration.between(amSelbenTag.getEnd(), urlaub.getBeginn()).toMinutes() < 90) {
                throw new Exception("Man muss mindestens 90 Minuten zwischen den beiden Urlaubsblöcken arbeiten");
            }
            if (!(urlaub.getEnd().compareTo(LocalTime.parse(ENDE_PRAKTIKUM)) == 0)) {
                throw new Exception("Der neue Urlaub muss am Ende des Tages liegen");
            }
        //der zuvor gebuchte Urlaub liegt am Ende der Praktikumszeit
        } else if (amSelbenTag.getEnd().compareTo(LocalTime.parse(ENDE_PRAKTIKUM)) == 0) {
            if (Duration.between(urlaub.getEnd(), amSelbenTag.getBeginn()).toMinutes() < 90) { //min. 90 arbeiten
                throw new Exception("Man muss mindestens 90 Minuten zwischen den beiden Urlaubsblöcken arbeiten");
            }
            if (!(urlaub.getBeginn().compareTo(LocalTime.parse(BEGINN_PRAKTIKUM)) == 0)) {
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
                    if (u.getBeginn().isAfter(pruefen.getEnd()) || u.getBeginn().equals(pruefen.getEnd())) {
                        //nichts erstatten: der zu buchende Urlaub ist gültig
                        stashUrlaube.add(pruefen);
                    }
                    //Startzeit von u liegt im neu zu buchenden Urlaub
                    else if ((u.getBeginn().isAfter(pruefen.getBeginn())) && u.getBeginn().isBefore(pruefen.getEnd())) {
                        //Endzeit von u liegt im neu zu buchenden Urlaub
                        if (u.getEnd().isBefore(pruefen.getEnd())) {
                            //Mitte erstatten: der geplante Urlaub urlaub wird in zwei Urlaubsblöcke aufgeteilt
                            Urlaub ersterBlock = new Urlaub(pruefen.getTag().toString(), pruefen.getBeginn().toString(), u.getBeginn().toString());
                            Urlaub zweiterBlock = new Urlaub(pruefen.getTag().toString(), u.getEnd().toString(), pruefen.getEnd().toString());
                            stashUrlaube.remove(pruefen);
                            stashUrlaube.add(ersterBlock);
                            stashUrlaube.add(zweiterBlock);
                            aenderung = true;
                            break;
                        }
                        //Endzeit von u ist nach urlaub oder gleichzeitig mit dessen Endzeit
                        else if (u.getEnd().isAfter(pruefen.getEnd()) || u.getEnd().equals(pruefen.getEnd())) {
                            //Ende erstatten
                            Urlaub stashUrlaub = new Urlaub(pruefen.getTag().toString(), pruefen.getBeginn().toString(), u.getBeginn().toString());
                            stashUrlaube.remove(pruefen);
                            stashUrlaube.add(stashUrlaub);
                            /* pruefen.setBis(u.getVon());*/
                            aenderung = true;
                            break;
                        }
                    }
                    //Startzeit von u ist vor urlaub oder gleichzeitig mit der Startzeit von urlaub
                    else if (u.getBeginn().isBefore(pruefen.getBeginn()) || u.getBeginn().equals(pruefen.getBeginn())) {
                        //Endzeit von u liegt in urlaub
                        if (u.getEnd().isAfter(pruefen.getBeginn()) && u.getEnd().isBefore(pruefen.getEnd())) {
                            //anfang erstatten
                            Urlaub stashUrlaub = new Urlaub(pruefen.getTag().toString(), u.getEnd().toString(), pruefen.getEnd().toString());
                            stashUrlaube.remove(pruefen);
                            stashUrlaube.add(stashUrlaub);
                            //pruefen.setVon(u.getBis());
                            aenderung = true;
                            break;
                        }
                        //Endzeit von u ist vor urlaub oder gleichzeitig mit dessen Startzeit
                        else if (u.getEnd().isBefore(pruefen.getBeginn()) || u.getEnd().equals(pruefen.getBeginn())) {
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
