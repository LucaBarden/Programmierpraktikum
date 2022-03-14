package de.propra.chicken.db.repo;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DBUtilRepository {

    @Query("SELECT LsfID FROM klausuren k INNER JOIN KlausurStudent ks ON ks.githubID = :id")
    List<Long> findKlausurenByStudent(@Param("id") long gitHubID);
}
