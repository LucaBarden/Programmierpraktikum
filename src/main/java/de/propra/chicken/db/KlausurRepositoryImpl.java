package de.propra.chicken.db;

import de.propra.chicken.application.service.repo.KlausurRepository;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.KlausurData;
import de.propra.chicken.domain.model.KlausurRef;

import java.util.HashSet;
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
    public Set<Klausur> getKlausurenByRefs(Set<KlausurRef> klausurenRef) {
        Set<Klausur> klausuren  = new HashSet<Klausur>();
        for (KlausurRef ref : klausurenRef) {
            klausuren.add(crudKlausur.findById(ref.getLsfID()).orElse(null));
        }
        return klausuren;
    }

    @Override
    public Set<KlausurData> getKlausurenDataByRefs(Set<KlausurRef> angemeldeteKlausurenRefs) {
        //TODO von klausur in klausurdata
        return null;
    }

    @Override
    public boolean validiereLsfIdCache(Klausur klausur) {
        //TODO validiere lsfid
        return false;
    }
}
