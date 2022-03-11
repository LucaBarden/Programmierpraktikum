package de.propra.chicken.application.service;

import de.propra.chicken.application.service.repository.StudentRepository;
import de.propra.chicken.domain.model.Student;

public class StudentService {


    private StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void save(Student student){
        //TODO validiere Student
        studentRepository.save(student);
    }

}