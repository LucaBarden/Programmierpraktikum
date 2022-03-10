package de.propra.chicken.db.repo;

import de.propra.chicken.domain.model.Klausur;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DBKlausurRepository extends CrudRepository<Klausur, Long> {
    List<Klausur> findAll();

    void saveKlausurStudent();

}
