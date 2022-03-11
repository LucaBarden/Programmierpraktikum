package de.propra.chicken.db;

import de.propra.chicken.application.service.repository.KlausurRepository;
import de.propra.chicken.db.repo.DBKlausurRepository;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KlausurRepositoryImpl implements KlausurRepository {
    private DBKlausurRepository repository;

    public KlausurRepositoryImpl(DBKlausurRepository repository) {
        this.repository = repository;
    }


    @Override
    public void save(Klausur klausur) {
        repository.save(klausur);
    }

    @Override
    public List<Klausur> findAll() {

       return repository.findAll();
    }

    @Override
    public void saveKlausurAnmeldung(Klausur klausur, Student student) {
        //TODO speicher Klausuranmeldung
    }

    @Override
    public List<Klausur> findAngemeldeteKlausuren(Student student) {
        //TODO lade alle Klausuranmeldungen
        return null;
    }

    @Override
    public void storniereKlausur(Klausur klausur, Student student) {
        //TODO storniere Klausuranmeldung
    }

    public boolean validiereKlausurAnmeldung(Klausur klausur, Student student){
        //TODO validiere Anmeldung
        return false;
    }



}
