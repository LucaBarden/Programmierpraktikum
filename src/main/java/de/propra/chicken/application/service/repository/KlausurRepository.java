package de.propra.chicken.application.service.repository;

import de.propra.chicken.domain.model.Klausur;

import java.util.List;

public interface KlausurRepository {
    void save(Klausur klausur);

    List<Klausur> findAll();
}
