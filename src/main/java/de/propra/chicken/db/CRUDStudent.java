package de.propra.chicken.db;

import de.propra.chicken.domain.model.KlausurData;
import de.propra.chicken.domain.model.KlausurRef;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CRUDStudent extends CrudRepository<StudentDTO, Long> {

    @Query("SELECT K.NAME, K.DATUM, K.BEGINN, K.ENDE, K.PRAESENZ FROM KLAUSUR K JOIN KLAUSUR_STUDENT KS ON K.LSF_ID = KS.KLAUSUR WHERE KS.STUDENT_DTO = :id")
    Set<KlausurData> findAngemeldeteKlausuren(@Param("id") long id);

    @Query("SELECT K.KLAUSUR FROM KLAUSUR_STUDENT K WHERE K.STUDENT_DTO = :id")
    Set<KlausurRef> findAngemeldeteKlausurenIds(@Param("id") long id);

    @Query("SELECT EXISTS(SELECT * FROM STUDENT WHERE STUDENT_ID = :id)")
    boolean existsById(@Param("id") long id);

    Optional<StudentDTO> findStudentDTOByGithubID(long id);
}
