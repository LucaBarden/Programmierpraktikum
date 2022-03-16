package de.propra.chicken.db;

import de.propra.chicken.domain.model.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CRUDStudent extends CrudRepository<Student, Long> {

    Student findByID(long githubID);
}
