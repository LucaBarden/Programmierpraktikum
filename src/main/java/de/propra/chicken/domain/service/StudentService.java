package de.propra.chicken.domain.service;

import de.propra.chicken.domain.model.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
@Service
public class StudentService {

    public Set<Urlaub> validiereKlausurAnmeldung(Klausur klausur,  Student student,String beginn, String ende ) throws Exception {

        //TODO: urlaube erstatten (wenn überlappt)
        Set<KlausurRef> angemeldeteKlausurenRefs = student.getKlausuren();

        //Die klausur ist mindestens einen Tag später
        if (!klausur.getDatum().isAfter(LocalDate.now())) {
            throw new Exception("Klausur findet heute statt. Anmeldung nicht mehr moeglich");
        }
        //Überprüft, ob der Student schon angemeldet ist
        for (KlausurRef tmpKlausur : angemeldeteKlausurenRefs) {
            if (tmpKlausur.getLsfID() == klausur.getLsfid()) {
                throw new Exception("Du bist bereits bei der Klausur angemeldet");
            }
        }
        //die urlaube, die man wieder bekommen würde durch neue Klausur: Zeitraum der Klausur mit puffer:
        Set<Urlaub> zuErstattendMoeglicheUrlaube = new HashSet<>();
        //TODO: hinterher wieder weg machen, kann man mit klausur als Parameter übergeben
        KlausurData klausurData = klausur.getKlausurData();

        klausurenAmSelbenTag(zuErstattendMoeglicheUrlaube, Set.of(klausurData), beginn, ende);
        Set<Urlaub> urlaubeSelberTag = student.urlaubSelberTag(klausurData.tag());
        
        return berechneGueltigeUrlaube(urlaubeSelberTag, zuErstattendMoeglicheUrlaube) ;
    }

    public Set<Urlaub> validiereUrlaub(Student student, Urlaub urlaub, Set<KlausurData> klausuren, String beginn, String ende) throws Exception {
        //TODO: String startdatum und String enddatum als Parameter übergeben und überprüfen, ob Urlaubsdatum im Praktikumszeitraum liegt (in checkAufGrundregeln())
        Set<Urlaub> zuErstattendeZeiten = new HashSet<>();

        checkAufGrundregeln(student.getResturlaub(), urlaub, beginn, ende);

        Set<Urlaub> urlaubeSelberTag = student.urlaubSelberTag(urlaub.getTag());
        Set<KlausurData> klausurSelberTag = klausurSelberTag(urlaub, klausuren);
        Set<Urlaub> neueUrlaube = new HashSet<>();

        //eine oder mehrere Klausuren am selben Tag: Urlaub darf frei eingeteilt werden
        //& überschneidende Urlaubszeit wird erstattet (keine Exceptions)
        if (!klausurSelberTag.isEmpty()) {
            klausurenAmSelbenTag(zuErstattendeZeiten, klausurSelberTag, beginn, ende);
            zuErstattendeZeiten.addAll(urlaubeSelberTag);
            Set<Urlaub> urlaube = new HashSet<>();
            urlaube.add(urlaub);
            neueUrlaube = berechneGueltigeUrlaube(urlaube, zuErstattendeZeiten);
        //keine Klausur am selben Tag: wenn neuer Urlaub nicht gültig, Exception werfen
        } else {
            keineKlausurSelberTag(urlaub, urlaubeSelberTag, beginn, ende);
            neueUrlaube.add(urlaub);
        }
        return neueUrlaube;
    }

    public void checkAufGrundregeln(int resturlaub, Urlaub urlaub, String beginn, String ende) throws Exception {
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
        if (resturlaub < Duration.between(urlaub.getBeginn(), urlaub.getEnd()).toMinutes()) {
            throw new Exception("Es ist zu wenig Resturlaub übrig");
        }
        if(urlaub.getBeginn().isBefore(LocalTime.parse(beginn)) || urlaub.getEnd().isAfter(LocalTime.parse(ende))) {
            throw new Exception("Der Urlaub muss im Praktikumszeitraum liegen");
        }
        if(urlaub.getTag().isBefore(LocalDate.now().plusDays(1))) {
            throw new Exception("Man kann Urlaub spätestens einen Tag vorher buchen.");
        }
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

    private void klausurenAmSelbenTag(Set<Urlaub> zuErstattenderUrlaube, Set<KlausurData> klausurSelberTag, String beginn, String ende) {
        for(KlausurData k : klausurSelberTag) {
            String klausurTag            = k.tag().toString();
            LocalTime anfangsZeitKlausur = k.von();
            LocalTime endZeitKlausur     = k.bis();

            if(k.isPraesenz()) {
                int pufferBefore = 120;
                int pufferAfter  = 120;
                erstattungsZeiten(zuErstattenderUrlaube, klausurTag, anfangsZeitKlausur, endZeitKlausur, pufferBefore, pufferAfter, beginn, ende);

            } else {
                int pufferBefore = 30;
                int pufferAfter  = 0;
                erstattungsZeiten(zuErstattenderUrlaube, klausurTag, anfangsZeitKlausur, endZeitKlausur, pufferBefore, pufferAfter, beginn, ende);
            }
        }
    }

    private void erstattungsZeiten(Set<Urlaub> zuErstattendeZeiten, String klausurTag, LocalTime anfangsZeitDerKlausur, LocalTime endZeitDerKlausur, int pufferBefore, int pufferAfter, String beginn, String ende) {
        //die Klausur fängt vor (Praktikumsbeginn + puffer) an: Urlaub wird ab Praktikumsbeginn erstattet
        if (anfangsZeitDerKlausur.isBefore(LocalTime.parse(beginn).plusMinutes(pufferBefore))) {
            //die Klausur endet nach (Praktikumsende - puffer): Urlaub wird bis Praktikumsende erstattet
            if (endZeitDerKlausur.isAfter(LocalTime.parse(ende).minusMinutes(pufferAfter))) {
                zuErstattendeZeiten.add(new Urlaub(klausurTag, beginn, ende));
            //die Klausur endet vor (Praktikumsende - puffer): Urlaub wird bis (Klausurende + puffer) erstattet
            } else {
                zuErstattendeZeiten.add(new Urlaub(klausurTag, beginn, endZeitDerKlausur.plusMinutes(pufferAfter).toString()));
            }
        //die Klausur fängt nach (Praktikumsbeginn + puffer) an: Urlaub wird ab (Klausurbeginn - puffer) erstattet
        } else {
            //die Klausur endet nach (Praktikumsende - puffer): Urlaub wird bis Praktikumsende erstattet
            if (endZeitDerKlausur.isAfter(LocalTime.parse(ende).minusMinutes(pufferAfter))) {
                zuErstattendeZeiten.add(new Urlaub(klausurTag, anfangsZeitDerKlausur.minusMinutes(pufferBefore).toString(), ende));
            //die Klausur endet vor (Praktikumsende - puffer): Urlaub wird bis (Klausurende + puffer) erstattet
            } else {
                zuErstattendeZeiten.add(new Urlaub(klausurTag, anfangsZeitDerKlausur.minusMinutes(pufferBefore).toString(), endZeitDerKlausur.plusMinutes(pufferAfter).toString()));
            }
        }
    }

    private void keineKlausurSelberTag(Urlaub urlaub, Set<Urlaub> urlaubeSelberTag, String beginn, String ende) throws Exception {
        //der anzulegende Urlaub ist der erste Urlaubsblock an dem Tag
        if (urlaubeSelberTag.isEmpty()) {
            //den ganzen Tag frei oder max 2,5 Stunden sind erlaubt
            if (Duration.between(urlaub.getBeginn(), urlaub.getEnd()).toMinutes() > 150 && Duration.between(urlaub.getBeginn(), urlaub.getEnd()).toMinutes() < 240) {
                throw new Exception("Der Urlaub ist weder den ganzen Tag lang noch weniger als 2.5 Stunden lang");
            }
            //schon 1 Urlaub am selben Tag
        } else if (urlaubeSelberTag.size() == 1) {
            //die zwei Urlaubsblöcke müssen am Anfang und Ende liegen mit mind. 90 Min Arbeitszeit dazwischen
            regelnFuerZweiBloecke(urlaub, urlaubeSelberTag, beginn, ende);
            //schon mehr als ein Urlaub am selben Tag
        } else{
            //kein weiterer Urlaub kann gebucht werden
            throw new Exception("Es wurden bereits zwei Urlaubsblöcke genommen");
        }
    }

    private void regelnFuerZweiBloecke(Urlaub urlaub, Set<Urlaub> urlaubeSelberTag, String beginn, String ende) throws Exception {
        Urlaub amSelbenTag = urlaubeSelberTag.stream().toList().get(0);
        //der zuvor gebuchte Urlaub liegt am Anfang der Praktikumszeit
        if (amSelbenTag.getBeginn().compareTo(LocalTime.parse(beginn)) == 0) {
            if (Duration.between(amSelbenTag.getEnd(), urlaub.getBeginn()).toMinutes() < 90) {
                throw new Exception("Man muss mindestens 90 Minuten zwischen den beiden Urlaubsblöcken arbeiten");
            }
            if (!(urlaub.getEnd().compareTo(LocalTime.parse(ende)) == 0)) {
                throw new Exception("Der neue Urlaub muss am Ende des Tages liegen");
            }
        //der zuvor gebuchte Urlaub liegt am Ende der Praktikumszeit
        } else if (amSelbenTag.getEnd().compareTo(LocalTime.parse(ende)) == 0) {
            if (Duration.between(urlaub.getEnd(), amSelbenTag.getBeginn()).toMinutes() < 90) { //min. 90 arbeiten
                throw new Exception("Man muss mindestens 90 Minuten zwischen den beiden Urlaubsblöcken arbeiten");
            }
            if (!(urlaub.getBeginn().compareTo(LocalTime.parse(beginn)) == 0)) {
                throw new Exception("Der neue Urlaub muss am Anfang des Tages liegen");
            }
            //der zuvor gebuchte Urlaub ist weder am Anfang noch am Ende des Tages
        } else {
            throw new Exception("Der bereits gebuchte Urlaub ist weder am Anfang noch am Ende des Tages");
        }
    }

    private Set<Urlaub> berechneGueltigeUrlaube(Set<Urlaub> urlaub, Set<Urlaub> zuErstattenderUrlaube) {
        Set<Urlaub> zuPruefendeUrlaube = new HashSet<>(urlaub);
        boolean aenderung = true;
            while (aenderung) {
                aenderung = false;
                for (Urlaub u : zuErstattenderUrlaube) {
                    for (Urlaub pruefen : zuPruefendeUrlaube) {
                        //Startzeit von u ist nach urlaub oder gleichzeitig mit dessen Endzeit
                        if (u.getBeginn().isAfter(pruefen.getEnd()) || u.getBeginn().equals(pruefen.getEnd())) {
                            //5: keine Überschneidung: nichts erstatten
                            zuPruefendeUrlaube.add(pruefen);
                        }
                        //Startzeit von u liegt im neu zu buchenden Urlaub
                        else if ((u.getBeginn().isAfter(pruefen.getBeginn())) && u.getBeginn().isBefore(pruefen.getEnd())) {
                            //Endzeit von u liegt im neu zu buchenden Urlaub
                            if (u.getEnd().isBefore(pruefen.getEnd())) {
                                //4: Mitte erstatten: der geplante Urlaub urlaub wird in zwei Urlaubsblöcke aufgeteilt
                                Urlaub ersterBlock = new Urlaub(pruefen.getTag().toString(), pruefen.getBeginn().toString(), u.getBeginn().toString());
                                Urlaub zweiterBlock = new Urlaub(pruefen.getTag().toString(), u.getEnd().toString(), pruefen.getEnd().toString());
                                zuPruefendeUrlaube.remove(pruefen);
                                zuPruefendeUrlaube.add(ersterBlock);
                                zuPruefendeUrlaube.add(zweiterBlock);
                                aenderung = true;
                                break;
                            }
                            //Endzeit von u ist nach urlaub oder gleichzeitig mit dessen Endzeit
                            else if (u.getEnd().isAfter(pruefen.getEnd()) || u.getEnd().equals(pruefen.getEnd())) {
                                //2: Ende erstatten
                                Urlaub stashUrlaub = new Urlaub(pruefen.getTag().toString(), pruefen.getBeginn().toString(), u.getBeginn().toString());
                                zuPruefendeUrlaube.remove(pruefen);
                                zuPruefendeUrlaube.add(stashUrlaub);
                                /* pruefen.setBis(u.getVon());*/
                                aenderung = true;
                                break;
                            }
                        }
                        //Startzeit von u ist vor urlaub oder gleichzeitig mit der Startzeit von urlaub
                        else if (u.getBeginn().isBefore(pruefen.getBeginn()) || u.getBeginn().equals(pruefen.getBeginn())) {
                            //Endzeit von u liegt in urlaub
                            if (u.getEnd().isAfter(pruefen.getBeginn()) && u.getEnd().isBefore(pruefen.getEnd())) {
                                //3: Anfang erstatten
                                Urlaub stashUrlaub = new Urlaub(pruefen.getTag().toString(), u.getEnd().toString(), pruefen.getEnd().toString());
                                zuPruefendeUrlaube.remove(pruefen);
                                zuPruefendeUrlaube.add(stashUrlaub);
                                //pruefen.setVon(u.getBis());
                                aenderung = true;
                                break;
                            }
                            //Endzeit von u ist vor urlaub oder gleichzeitig mit dessen Startzeit
                            else if (u.getEnd().isBefore(pruefen.getBeginn()) || u.getEnd().equals(pruefen.getBeginn())) {
                                //5: keine Überschneidung: nichts erstatten
                                zuPruefendeUrlaube.add(pruefen);
                            }
                            else if(u.getEnd().equals(pruefen.getEnd()) || u.getEnd().isAfter(pruefen.getEnd())) {
                                //1: der gesamte Urlaub wird erstattet
                                zuPruefendeUrlaube.remove(pruefen);
                                aenderung = true;
                                break;
                            }
                        }
                    }
                }
            }
        return zuPruefendeUrlaube;
    }


    public Map<Urlaub, Boolean> stornierbareUrlaube(Set<Urlaub> urlaube) {
        //prüft, ob der Urlaub noch im stornierbaren Zeitraum ist
        Map<Urlaub, Boolean> stornierbar = new HashMap<>();
        for(Urlaub urlaub : urlaube) {
            if(urlaub.getTag().isAfter(LocalDate.now())) {
                stornierbar.put(urlaub, true);
            }
            else {
                stornierbar.put(urlaub, false);
            }
        }
        return stornierbar;
    }
}
