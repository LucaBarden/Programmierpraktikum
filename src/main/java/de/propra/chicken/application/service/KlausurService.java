package de.propra.chicken.application.service;

import de.propra.chicken.application.service.repository.KlausurRepository;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.service.KlausurDomainService;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class KlausurService {

    private final KlausurRepository klausurRepository;

    public KlausurService(KlausurRepository klausurRepository) {
        this.klausurRepository = klausurRepository;
    }

    public void anmelden(Klausur klausur, Student student) throws Exception {
        try {
            klausurRepository.validiereKlausurAnmeldung(klausur, student);
            klausurRepository.saveKlausurAnmeldung(klausur, student);
        } catch (Exception ex) {
            throw ex;
        }
    }


    public void save(Klausur klausur) throws Exception {
        try {
            validiereKlausur(klausur);
            klausurRepository.save(klausur);
        } catch (Exception ex) {
            throw ex;
        }

    }

    private void validiereKlausur(Klausur klausur) throws Exception {
        //TODO Auf Namen der Klausur prüfen
        String webContent = Jsoup.connect(String.format("https://lsf.hhu.de/qisserver/rds?state=verpublish" +
                "&status=init&vmfile=no&publishid=%d&moduleCall=webInfo&publishConfFile=webInfo&publishSubDir=veranstaltung", klausur.getLsfid())).get().text();
        //String.valueOf(klausur.getLsfid())
        if(!(webContent.contains("VeranstaltungsID"))){
            throw new IllegalArgumentException("Invalide LSF ID");
        }
    }

    public List<Klausur> findAll() {
        List<Klausur> alleKlausuren = klausurRepository.findAll();
        return KlausurDomainService.validiereAlleKlausuren(alleKlausuren);
    }

    public Map<Klausur, Boolean> findAngemeldeteKlausuren(Student student) {
        List<Klausur> klausuren = klausurRepository.findAngemeldeteKlausuren(student);
        return KlausurDomainService.stornierbareKlausuren(klausuren);
    }

    public void storniereKlausur(Klausur klausur, Student student) {
        klausurRepository.storniereKlausur(klausur, student);
    }

}
