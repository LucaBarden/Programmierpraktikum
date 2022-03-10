package de.propra.chicken.application.service.repository;

import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;

import java.util.List;

public interface UrlaubRepository {
    void save(Urlaub urlaub);

    List<Urlaub> findAllByID(Student student);
}
