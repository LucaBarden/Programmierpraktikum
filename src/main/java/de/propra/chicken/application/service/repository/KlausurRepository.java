package de.propra.chicken.application.service.repository;

import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;

import java.util.List;

public interface KlausurRepository {
    void save(Klausur klausur);

    List<Klausur> findAll();

    void saveKlausurAnmeldung(Klausur klausur, Student student);

    List<Klausur> findAngemeldeteKlausuren(Student student);

    void storniereKlausur(Student student, Klausur klausur);
}
