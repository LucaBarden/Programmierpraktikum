package de.propra.chicken.db.repo;

import de.propra.chicken.domain.model.Urlaub;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DBUrlaubRepository extends CrudRepository<Urlaub, Long> {
    List<Urlaub> findAllById(Long githubID);
}
