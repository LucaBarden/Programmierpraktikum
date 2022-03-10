package de.propra.chicken.db.repo;

import de.propra.chicken.domain.model.Student;
import org.springframework.data.repository.CrudRepository;

public interface DBStudentRepository extends CrudRepository<Student, Long> {

}
