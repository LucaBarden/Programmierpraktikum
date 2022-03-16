package de.propra.chicken.db;

import de.propra.chicken.domain.model.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CRUDStudent extends CrudRepository<Student, Long> {
}
