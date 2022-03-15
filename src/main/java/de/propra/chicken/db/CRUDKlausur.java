package de.propra.chicken.db;

import de.propra.chicken.domain.model.Klausur;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CRUDKlausur extends CrudRepository<Klausur, Long> {
    @Override
    Set<Klausur> findAll();
}
