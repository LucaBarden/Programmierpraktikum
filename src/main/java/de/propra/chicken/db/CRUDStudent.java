package de.propra.chicken.db;

import de.propra.chicken.domain.model.KlausurData;
import de.propra.chicken.domain.model.KlausurRef;
import de.propra.chicken.domain.model.Urlaub;
import de.propra.chicken.domain.dto.StudentDTO;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CRUDStudent extends CrudRepository<StudentDTO, Long> {

    @Query("select k.name, k.datum, k.beginn, k.beginn, k.praesenz from klausur k join klausur_student ks on k.lsf_id = ks.klausur where ks.student = :id")
    Set<KlausurData> findAngemeldeteKlausuren(@Param("id") long id);

    @Query("select k.klausur from klausur_student k where k.student = :id")
    Set<KlausurRef> findAngemeldeteKlausurenIds(@Param("id") long id);

    @Query("select * from urlaub u where u.github_id = :id")
    Set<Urlaub> getUrlaube(@Param("id") long id);

    @Query("select exists(select * from student where github_id = :id)")
    boolean existsById(@Param("id") long id);

    StudentDTO findByGithubID(long id);

    @Query("INSERT INTO student(github_id, resturlaub) VALUES( )")
    StudentDTO speicherStudent(StudentDTO dto);
}
