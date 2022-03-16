package de.propra.chicken.db;

import de.propra.chicken.application.service.repo.KlausurRepository;
import de.propra.chicken.domain.model.Klausur;

import java.util.Set;

public class KlausurRepositoryImpl implements KlausurRepository {

    private CRUDKlausur crudKlausur;

    public KlausurRepositoryImpl(CRUDKlausur crudKlausur) {
        this.crudKlausur = crudKlausur;
    }

    @Override
    public void speicherKlausur(Klausur klausur) {
        //TODO save Klausur
    }

    @Override
    public Set<Klausur> ladeAlleKlausuren() {
        return crudKlausur.findAll();
    }

    @Override
    public boolean validiereLsfIdCache(Klausur klausur) {
        //TODO validiere lsfid
        return false;
    }
}
