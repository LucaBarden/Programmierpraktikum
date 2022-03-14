package de.propra.chicken.db;

import de.propra.chicken.application.service.repository.UtilRepository;
import de.propra.chicken.db.repo.DBUtilRepository;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public class UtilRepositoryImpl implements UtilRepository {

    private DBUtilRepository repository;

    public UtilRepositoryImpl(DBUtilRepository repository) {
        this.repository = repository;
    }





}
