package de.propra.chicken.application.service;

import de.propra.chicken.application.service.repository.StudentRepository;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;

public class StudentService {


    private StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void save(Student student){
        studentRepository.save(student);
    }

}
