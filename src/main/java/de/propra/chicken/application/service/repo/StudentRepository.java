package de.propra.chicken.application.service.repo;

import de.propra.chicken.domain.model.*;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface StudentRepository {
    Set<KlausurRef> getKlausurenVonStudent(Student student);

    Set<Urlaub> getUrlaubeVonStudent(Student student);

    Student speicherKlausurAnmeldung(Student student);

    Set<KlausurData> findAngemeldeteKlausuren(long githubID);

    Student speicherStudent(Student student);

    Student findByID(long githubID);

    Set<KlausurRef> findAngemeldeteKlausurenIds(long githubID);
}
