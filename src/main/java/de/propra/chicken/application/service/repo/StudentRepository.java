package de.propra.chicken.application.service.repo;

import de.propra.chicken.domain.model.KlausurRef;
import de.propra.chicken.domain.model.Student;

import java.util.Set;


public interface StudentRepository {


    Student speicherStudent(Student student);

    Student findByID(long githubID) throws Exception;

    Set<KlausurRef> getAngemeldeteKlausurenIds(long githubID);

    boolean existsById(long githubID);


}
