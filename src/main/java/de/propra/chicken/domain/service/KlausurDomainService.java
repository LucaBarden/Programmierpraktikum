package de.propra.chicken.domain.service;

import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class KlausurDomainService {

    public static void validiereKlausur(Klausur klausur) throws Exception {
        //TODO validiereKlausur()
        throw new Exception("Klausur ist nicht valide");
    }

    public static void validiereKlausurAnmeldung(Klausur klausur, Student student) throws Exception {
        //TODO validiere Anmeldung
        throw new Exception("klausuranmeldung ist nicht valide");
    }

    public static void validiereKlausurStornierung(Klausur klausur, Student student) throws Exception {
        //TODO validiere Stornierung
        throw new Exception("klausurstornierung");
    }


    public static List<Klausur> validiereAlleKlausuren(List<Klausur> alleKlausuren) {
        //TODO richtige Überprüfung? Ein Tag vor Klausur Tag
        List<Klausur> gueltigeKlausuren = new LinkedList<>();
        for(Klausur klausur : alleKlausuren) {
            if(klausur.getDate().isAfter(LocalDate.now())) {
                gueltigeKlausuren.add(klausur);
            }
        }
        return gueltigeKlausuren;


    }
}
