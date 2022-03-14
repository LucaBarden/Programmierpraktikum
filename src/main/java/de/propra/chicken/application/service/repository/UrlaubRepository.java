package de.propra.chicken.application.service.repository;

import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UrlaubRepository {
    void save(Student student, Urlaub urlaub);

    List<Urlaub> findAllByID(long githubID);

}
