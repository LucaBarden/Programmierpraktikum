package de.propra.chicken.application.service;

import de.propra.chicken.application.service.repository.KlausurRepository;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.service.KlausurDomainService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KlausurService {

    private KlausurRepository klausurRepository;

    public KlausurService(KlausurRepository klausurRepository) {
        this.klausurRepository = klausurRepository;
    }

    public void anmelden(Klausur klausur, Student student) throws Exception {
        try {
            KlausurDomainService.validiereKlausurAnmeldung(klausur, student);
            klausurRepository.saveKlausurAnmeldung(klausur, student);
        }
        catch (Exception ex) {
            throw ex;
        }


    }

    public void save(Klausur klausur) throws Exception{
        try {
            KlausurDomainService.validiereKlausur(klausur);
            klausurRepository.save(klausur);
        }
        catch(Exception ex) {
            throw ex;
        }

    }

    public List<Klausur> findAll(){
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
