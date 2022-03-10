package de.propra.chicken.db.repo;

import de.propra.chicken.domain.model.Klausur;
import org.springframework.data.repository.CrudRepository;

public interface DBKlausurRepository extends CrudRepository<Klausur, Long> {
}
