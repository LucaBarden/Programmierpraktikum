package de.propra.chicken.db;

import de.propra.chicken.application.service.repo.StudentRepository;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.KlausurRef;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;

import java.util.Set;

public class StudentRepositoryImpl implements StudentRepository {

    private CRUDStudent crudStudent;

    public StudentRepositoryImpl(CRUDStudent crudStudent) {
        this.crudStudent = crudStudent;
    }

    @Override
    public Set<Klausur> getKlausurenVonStudent(Student student) {
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
    public Set<KlausurRef> findAngemeldeteKlausuren(long gihubID) {
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
