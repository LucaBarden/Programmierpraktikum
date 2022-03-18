package de.propra.chicken.application.service.repo;

import de.propra.chicken.domain.model.*;

import java.util.Set;


public interface StudentRepository {

    Set<Urlaub> getUrlaubeVonStudent(Student student);

    Student speicherKlausurAnmeldung(Student student);

    Set<KlausurData> findAngemeldeteKlausuren(long githubID);

    Student speicherStudent(Student student);

    Student findByID(long githubID);

    Set<KlausurRef> getAngemeldeteKlausurenIds(long githubID);


}
