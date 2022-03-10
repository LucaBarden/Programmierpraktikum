package de.propra.chicken.application.service;

import de.propra.chicken.application.service.repository.KlausurRepository;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.service.KlausurDomainService;
import org.springframework.beans.factory.annotation.Autowired;

public class KlausurService {

    KlausurRepository klausurRepository;

    public KlausurService(KlausurRepository klausurRepository) {
        this.klausurRepository = klausurRepository;
    }



    public void save(Klausur klausur){
        validiereKlausur(klausur);
        klausurRepository.save(klausur);
    }
    public void findAll(){
        klausurRepository.findAll();
    }

    private void validiereKlausur(Klausur klausur) {
        try{
            KlausurDomainService.validiereKlausur(klausur);
        } catch (Exception e) {
            //TODO behandleException klausurinvalide
        }
    }
}
