package de.propra.chicken.db;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@JdbcTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {CRUDKlausur.class, CRUDKlausur.class})
@TestPropertySource(locations = {"classpath:testEnv"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class KlausurRepositoryImplTest {

    @Autowired
    CRUDKlausur db;

    @Test
    @Sql({"classpath:db/migration/V1__init.sql"})
    @DisplayName("MyTest")
    void test1() {
    }
}
