package de.propra.chicken.db;

import de.propra.chicken.application.service.repo.StudentRepository;
import de.propra.chicken.domain.model.KlausurRef;
import de.propra.chicken.domain.model.Student;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class StudentRepositoryImpl implements StudentRepository {


    private final CRUDStudent crudStudent;

    public StudentRepositoryImpl(CRUDStudent crudStudent) {
        this.crudStudent = crudStudent;
    }


    @Override
    public Student speicherStudent(Student student) {
        StudentDTO dto = getDto(student);
        if(crudStudent.existsById(student.getGithubID())) {
            dto.setIsNew(false);
        }
        StudentDTO newDto = crudStudent.save(dto);
        Set<KlausurRef> refs = crudStudent.findAngemeldeteKlausurenIds(student.getGithubID());
        return transferDTOToStudent(newDto, refs);
    }


    @Override
    public Student findByID(long githubID) throws Exception{
        StudentDTO dto = crudStudent.findStudentDTOByGithubID(githubID).orElseThrow(() -> new Exception("Dieser Student existiert nicht"));
        Set<KlausurRef> refs = crudStudent.findAngemeldeteKlausurenIds(dto.getId());
        return transferDTOToStudent(dto, refs);
    }


    @Override
    public Set<KlausurRef> getAngemeldeteKlausurenIds(long githubID) {
        return crudStudent.findAngemeldeteKlausurenIds(githubID);
    }


    @Override
    public boolean existsById(long githubID) {
        return crudStudent.existsById(githubID);
    }

    Student transferDTOToStudent(StudentDTO dto, Set<KlausurRef> refs) {
        Student student = new Student(dto.getGithubID(), dto.getResturlaub());
        student.setUrlaube(dto.getUrlaube());
        student.setKlausuren(refs);
        return student;
    }


    public StudentDTO getDto(Student student) {
        return new StudentDTO(student.getGithubID(), student.getResturlaub(), student.getUrlaube().stream().map(u -> new UrlaubDTO(u.getTag().toString(), u.getBeginn().toString(), u.getEnd().toString())).collect(Collectors.toSet()), student.getKlausuren());
    }


}
