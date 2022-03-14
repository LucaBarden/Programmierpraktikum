package de.propra.chicken.application.service;

import de.propra.chicken.application.service.repository.KlausurRepository;
import de.propra.chicken.application.service.repository.UrlaubRepository;
import de.propra.chicken.application.service.repository.UtilRepository;
import de.propra.chicken.db.UtilRepositoryImpl;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;

import java.util.List;

public class UrlaubService {


    private UrlaubRepository urlaubRepository;



    public UrlaubService(UrlaubRepository urlaubRepository) {
        this.urlaubRepository = urlaubRepository;
    }

    public void save(Student student, Urlaub urlaub ) {
        validiereUrlaub(student, urlaub);
        urlaubRepository.save(student, urlaub);
    }



    private void validiereUrlaub(Student student, Urlaub urlaub) {
        List<Urlaub> urlaube = findUrlaube(student);


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
    }

    private List<Urlaub> findUrlaube(Student student) {

        return urlaubRepository.findAllByID(student.getGithubID());
    }


}

