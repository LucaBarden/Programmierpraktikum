package de.propra.chicken.db;

import de.propra.chicken.application.service.repository.StudentRepository;
import de.propra.chicken.db.repo.DBStudentRepository;
import de.propra.chicken.domain.model.Student;
import org.springframework.stereotype.Repository;


public class StudentRepositoryImpl implements StudentRepository {

    DBStudentRepository studentRepository;

    @Override
    public void save(Student student) {
        studentRepository.save(student);

    }
}
