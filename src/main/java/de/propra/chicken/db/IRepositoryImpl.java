package de.propra.chicken.db;

import de.propra.chicken.application.service.repo.IRepository;
import de.propra.chicken.domain.model.Klausur;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;

import java.util.Set;

public class IRepositoryImpl implements IRepository {


    private CRUDKlausur crudKlausur;
    private CRUDStudent crudStudent;

    public IRepositoryImpl(CRUDKlausur crudKlausur, CRUDStudent crudStudent) {
        this.crudKlausur = crudKlausur;
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
    public boolean validiereLsfIdCache(Klausur klausur) {
        //TODO validiere lsfid
        return false;
    }

    @Override
    public void speicherKlausur(Klausur klausur) {
        //TODO save Klausur
    }

    @Override
    public Set<Klausur> ladeAlleKlausuren() {
        return crudKlausur.findAll();
    }

    @Override
    public Set<Klausur> findAngemeldeteKlausuren(Student student) {
        //TODO lade alle Klausuranmeldungen
        return null;
    }

    @Override
    public Student speicherStudent(Student student) {
        return crudStudent.save(student);
    }
}
