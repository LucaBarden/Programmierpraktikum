package de.propra.chicken.db;

import de.propra.chicken.application.service.repository.UrlaubRepository;
import de.propra.chicken.db.repo.DBUrlaubRepository;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UrlaubRepositoryImpl implements UrlaubRepository {
    private DBUrlaubRepository repository;

    public UrlaubRepositoryImpl(DBUrlaubRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Student student, Urlaub urlaub) {
        repository.save(urlaub);
    }


    public List<Urlaub> findAllByID(long githubID) {
        repository.findAllById(githubID);
        return null;
    }


    public List<Long> findKlausurenby(long githubID) {
        return null;
    }
}

//    @Query("SELECT lsfID FROM klausuren k INNER JOIN KlausurStudent ks ON ks.githubID = :id")
//    List<Long> findKlausurenByStudent(@Param("id") long gitHubID);
