package de.propra.chicken.db;

import de.propra.chicken.application.service.repository.UrlaubRepository;
import de.propra.chicken.db.repo.DBKlausurRepository;
import de.propra.chicken.db.repo.DBUrlaubRepository;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Urlaub;
import org.springframework.stereotype.Repository;

@Repository
public class UrlaubRepositoryImpl implements UrlaubRepository {
    private DBUrlaubRepository repository;

    public UrlaubRepositoryImpl(DBUrlaubRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Urlaub urlaub) {
        repository.save(urlaub);
    }
}
