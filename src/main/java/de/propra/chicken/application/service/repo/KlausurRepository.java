package de.propra.chicken.application.service.repo;

import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.KlausurData;
import de.propra.chicken.domain.model.KlausurRef;

import java.util.Set;

public interface KlausurRepository {

    Klausur speicherKlausur(Klausur klausur);

    Set<Klausur> ladeAlleKlausuren();

    Set<Klausur> getKlausurenByRefs(Set<KlausurRef> klausuren);

    Set<KlausurData> getKlausurenDataByRefs(Set<KlausurRef> angemeldeteKlausurenRefs);

    Klausur findeKlausurByID(long id) throws Exception;
}
