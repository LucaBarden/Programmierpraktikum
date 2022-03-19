package de.propra.chicken.db;

import de.propra.chicken.domain.model.Klausur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@JdbcTest
@ActiveProfiles("test")
//@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {CRUDStudent.class, CRUDKlausur.class})
@TestPropertySource(locations = {"classpath:testEnv"})
public class KlausurRepositoryImplTest {

    @Autowired
    CRUDKlausur db;

    @Test
    @Sql({"classpath:db/migration/V1__init.sql"})
    @DisplayName("MyTest")
    void test1() {

    }
}
