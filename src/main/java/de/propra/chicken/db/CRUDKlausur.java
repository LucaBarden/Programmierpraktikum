package de.propra.chicken.db;

import de.propra.chicken.db.dto.KlausurDTO;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CRUDKlausur extends CrudRepository<KlausurDTO, Long> {
    @Override
    Set<KlausurDTO> findAll();

    @Query("select * from klausur k where k.lsf_id = :id")
    Set<KlausurDTO> findeKlausurByID(@Param("id") long id);



}
