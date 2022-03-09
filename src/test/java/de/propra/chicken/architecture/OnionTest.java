package de.propra.chicken.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Disabled;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

@AnalyzeClasses(packages = "de.propra.chicken")
public class OnionTest {

    @Disabled
    @ArchTest
    public ArchRule onionTest() {
        ArchRule onionTest = onionArchitecture()
                .domainModels("..domain.model..")
                .domainServices("..domain.service..")
                .applicationServices("..service..")

                .adapter("web", "..web..")
                .adapter("db", "..repository..");
        return onionTest;
    }


}