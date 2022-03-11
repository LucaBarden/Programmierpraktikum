package de.propra.chicken.application.service;

import de.propra.chicken.application.service.repository.KlausurRepository;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.service.KlausurDomainService;

import java.util.List;

public class KlausurService {

    private KlausurRepository klausurRepository;

    public KlausurService(KlausurRepository klausurRepository) {
        this.klausurRepository = klausurRepository;
    }

    public void anmelden(Klausur klausur, Student student) throws Exception {
        try {
            validiereKlausurAnmeldung(klausur, student);
            klausurRepository.saveKlausurAnmeldung(klausur, student);
        }
        catch (Exception ex) {
            throw ex;
        }


    }

    public void save(Klausur klausur) throws Exception{
        try {
            validiereKlausur(klausur);
            klausurRepository.save(klausur);
        }
        catch(Exception ex) {
            throw ex;
        }

    }

    public List<Klausur> findAll(){
        return klausurRepository.findAll();
    }

    public List<Klausur> findAngemeldeteKlausuren(Student student) {
        return klausurRepository.findAngemeldeteKlausuren(student);
    }

    public void storniereKlausur(Student student, Klausur klausur) throws Exception {
        validiereKlausurStornierung(student, klausur);
        klausurRepository.storniereKlausur(student, klausur);
    }

    private void validiereKlausurStornierung(Student student, Klausur klausur) throws Exception {
        try {
            KlausurDomainService.validiereKlausurStornierung();
        }
        catch (Exception ex) {
            throw ex;
        }
    }


    private void validiereKlausurAnmeldung(Klausur klausur, Student student) throws Exception {
        try {
            KlausurDomainService.validiereKlausurAnmeldung();
        }
        catch(Exception ex) {
            throw ex;
        }
    }



    private void validiereKlausur(Klausur klausur) {
        try{
            KlausurDomainService.validiereKlausur(klausur);
        } catch (Exception e) {
            //TODO behandleException klausurinvalide
        }
    }
}
