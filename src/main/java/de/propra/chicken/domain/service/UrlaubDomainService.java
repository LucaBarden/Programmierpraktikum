package de.propra.chicken.domain.service;

import de.propra.chicken.domain.model.Urlaub;

public class UrlaubDomainService {


    public static void validiereUrlaub(Urlaub urlaub) throws Exception{
        //TODO validiereUrlaub
        /*
        - ganze Viertelstunden & Startzeiten 00, 15, 30, 45
        - Buchungszeit <= Resturlaub
        - bei angemeldeter Klausur, überschneidende Urlaubszeit erstatten
        - ein Block pro Tag: - den ganzen Tag frei
                             - max 2,5 Stunden frei
        - zwei Blöcke pro Tag: - müssen am Anfang und am Ende sein
                               - mindestens 90 Min Arbeitszeit zwischen dem Urlaub
        - bei Klausuranmeldung an dem Tag: Urlaub darf frei eingeteilt werden
         */
        throw new Exception("Nicht valide");
    }

}
