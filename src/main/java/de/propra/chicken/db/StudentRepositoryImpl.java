package de.propra.chicken.db;

import de.propra.chicken.application.service.repo.StudentRepository;
import de.propra.chicken.domain.model.*;
import de.propra.chicken.dto.StudentDTO;
import org.springframework.stereotype.Repository;

import java.util.Set;
@Repository
public class StudentRepositoryImpl implements StudentRepository {

    private CRUDStudent crudStudent;

    public StudentRepositoryImpl(CRUDStudent crudStudent) {
        this.crudStudent = crudStudent;
    }


    @Override
    public Set<Urlaub> getUrlaubeVonStudent(Student student) {
        return crudStudent.getUrlaube(student.getGithubID());
    }

    @Override
    public Student speicherKlausurAnmeldung(Student student) {
        return transferDTOToStudent(crudStudent.save(transferStudentToDTO(student)));
    }

    @Override
    public Set<KlausurData> findAngemeldeteKlausuren(long githubID) {
        return crudStudent.findAngemeldeteKlausuren(githubID);
    }

    @Override
    public Student speicherStudent(Student student) {
        return transferDTOToStudent(crudStudent.save(transferStudentToDTO(student)));
    }

    @Override
    public Student findByID(long githubID) {
        return transferDTOToStudent(crudStudent.findById(githubID).orElse(null));
    }

    @Override
    public Set<KlausurRef> getAngemeldeteKlausurenIds(long githubID) {
        return crudStudent.findAngemeldeteKlausurenIds(githubID);
    }

    private Student transferDTOToStudent(StudentDTO dto) {
        return new Student(dto.getGithubID(), dto.getResturlaub());

    }

    private StudentDTO transferStudentToDTO(Student s){
        return new StudentDTO(s);
    }
}
