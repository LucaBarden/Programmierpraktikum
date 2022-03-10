package de.propra.chicken.db.repo;

import de.propra.chicken.domain.model.Urlaub;
import org.springframework.data.repository.CrudRepository;

public interface DBUrlaubRepository extends CrudRepository<Urlaub, Long> {
}
