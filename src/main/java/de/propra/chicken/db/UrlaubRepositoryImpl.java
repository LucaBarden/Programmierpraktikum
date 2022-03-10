package de.propra.chicken.db;

import de.propra.chicken.application.service.repository.UrlaubRepository;
import de.propra.chicken.db.repo.DBUrlaubRepository;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Override
    public List<Urlaub> findAllByID(Student student) {

        //TODO lade alle Urlaube
        return null;
    }
}
