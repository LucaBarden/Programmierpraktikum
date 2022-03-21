package de.propra.chicken.db;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CRUDKlausur extends CrudRepository<KlausurDTO, Long> {
    @Override
    Set<KlausurDTO> findAll();

    @Query("select k.* from KLAUSUR k where k.LSF_ID = :id")
    Optional<KlausurDTO> findeKlausurByID(@Param("id") long id);




}
