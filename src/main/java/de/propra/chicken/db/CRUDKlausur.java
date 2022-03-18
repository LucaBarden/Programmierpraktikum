package de.propra.chicken.db;

import de.propra.chicken.dto.KlausurDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CRUDKlausur extends CrudRepository<KlausurDTO, Long> {

    Set<KlausurDTO> findAll();

    Set<KlausurDTO> findAllByID();

}
