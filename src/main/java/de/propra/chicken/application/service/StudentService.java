package de.propra.chicken.application.service;

import de.propra.chicken.application.service.repository.StudentRepository;
import de.propra.chicken.domain.model.Student;
import org.springframework.beans.factory.annotation.Autowired;

public class StudentService {

    @Autowired
    StudentRepository studentRepository;

    public void save(Student student){
        studentRepository.save(student);
    }
}
