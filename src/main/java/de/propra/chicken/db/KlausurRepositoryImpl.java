package de.propra.chicken.db;

import de.propra.chicken.application.service.repository.KlausurRepository;
import de.propra.chicken.db.repo.DBKlausurRepository;
import de.propra.chicken.domain.model.Klausur;
import org.springframework.stereotype.Repository;

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
}
