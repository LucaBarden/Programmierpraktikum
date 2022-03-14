package de.propra.chicken.db.repo;

import de.propra.chicken.domain.model.Klausur;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DBKlausurRepository extends CrudRepository<Klausur, Long> {
    List<Klausur> findAll();



}
