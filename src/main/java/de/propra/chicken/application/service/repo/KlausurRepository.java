package de.propra.chicken.application.service.repo;

import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.KlausurRef;

import java.util.Set;

public interface KlausurRepository {
    boolean validiereLsfIdCache(Klausur klausur);

    void speicherKlausur(Klausur klausur);

    Set<Klausur> ladeAlleKlausuren();

    Set<Klausur> getKlausurenByRefs(Set<KlausurRef> klausuren);

}
