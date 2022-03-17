package de.propra.chicken.db;

import de.propra.chicken.application.service.repo.StudentRepository;
import de.propra.chicken.domain.model.*;

import java.util.Set;

public class StudentRepositoryImpl implements StudentRepository {

    private CRUDStudent crudStudent;

    public StudentRepositoryImpl(CRUDStudent crudStudent) {
        this.crudStudent = crudStudent;
    }

    @Override
    public Set<KlausurRef> getKlausurenVonStudent(Student student) {
        //TODO DB Query
        return null;
    }

    @Override
    public Set<Urlaub> getUrlaubeVonStudent(Student student) {
        //TODO DB Query
        return null;
    }

    @Override
    public Student speicherKlausurAnmeldung(Student student) {

        return crudStudent.save(student);
    }

    @Override
    public Set<KlausurData> findAngemeldeteKlausuren(long gihubID) {
        //TODO lade alle Klausuranmeldungen
        return null;
    }

    @Override
    public Student speicherStudent(Student student) {
        return crudStudent.save(student);
    }

    @Override
    public Student findByID(long githubID) {
        return crudStudent.findById(githubID).orElse(null);
    }

}
