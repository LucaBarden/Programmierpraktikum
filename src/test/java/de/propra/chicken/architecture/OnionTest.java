package de.propra.chicken.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

@AnalyzeClasses(packages = "de.propra.chicken", importOptions = { ImportOption.DoNotIncludeTests.class })
public class OnionTest {

    @ArchTest
    static final ArchRule onionTest = onionArchitecture()
                .domainModels("..domain.model..")
                .domainServices("..domain.service..")
                .applicationServices("..application.service..")

                .adapter("web", "..web..")
                .adapter("db", "..db..");

    //@Disabled
    /*@ArchTest
    public ArchRule onionTest() {
        ArchRule onionTest = onionArchitecture()
                .domainModels("..domain.model..")
                //.domainServices("..domain.service..")
                .applicationServices("..application.service..")

                .adapter("web", "..web..")
                .adapter("db", "..db..");
        return onionTest;
    }*/

}