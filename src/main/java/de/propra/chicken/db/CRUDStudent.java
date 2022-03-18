package de.propra.chicken.db;

import de.propra.chicken.domain.model.KlausurData;
import de.propra.chicken.domain.model.KlausurRef;
import de.propra.chicken.domain.model.Student;
import de.propra.chicken.domain.model.Urlaub;
import de.propra.chicken.dto.StudentDTO;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CRUDStudent extends CrudRepository<StudentDTO, Long> {

    @Query("select k.name, k.datum, k.beginn, k.schluss, k.isPraesenz from klausur k join klausur_student ks on k.lsfID = ks.lsfID where ks.student = :id")
    Set<KlausurData> findAngemeldeteKlausuren(@Param("id") long id);

    @Query("select k.klausur from klausur_student k where ks.student = :id")
    Set<KlausurRef> findAngemeldeteKlausurenIds(@Param("id") long id);

    @Query("select u.* from urlaub u where u.githubID = :id")
    Set<Urlaub> getUrlaube(@Param("id") long id);
}
