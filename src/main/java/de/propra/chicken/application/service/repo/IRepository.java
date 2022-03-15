package de.propra.chicken.application.service.repo;

import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface IRepository {
    Set<Klausur> getKlausurenVonStudent(Student student);

    Set<Urlaub> getUrlaubeVonStudent(Student student);

    Student speicherKlausurAnmeldung(Student student);

    boolean validiereLsfIdCache(Klausur klausur);

    void speicherKlausur(Klausur klausur);

    Set<Klausur> ladeAlleKlausuren();

    Set<Klausur> findAngemeldeteKlausuren(Student student);

    Student speicherStudent(Student student);
}
