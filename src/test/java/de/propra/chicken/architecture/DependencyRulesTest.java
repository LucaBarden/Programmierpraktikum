package de.propra.chicken.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES;

@AnalyzeClasses(packages = "de.propra.chicken", importOptions = { ImportOption.DoNotIncludeTests.class })
public class DependencyRulesTest {

    @ArchTest
    static final ArchRule no_accesses_to_upper_package = NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES;

    @ArchTest
    ArchRule cR2 = GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME;

    @ArchTest
    ArchRule fieldsInCOntrollersShouldBePrivate =
            fields().that().areDeclaredInClassesThat().areAnnotatedWith(Controller.class).should().bePrivate();

    @ArchTest
    ArchRule onlyControllerClAccessToCoCl =
            noClasses().that().areNotAnnotatedWith(Controller.class)
                    .should().accessClassesThat().areAnnotatedWith(Controller.class);

    @ArchTest
    ArchRule serviceClasseswithServiceAnnot =
            classes()
                    .that().areNotInterfaces()
                    .and().areTopLevelClasses().and()
                    .resideInAPackage("..service..")
                    .should()
                    .beAnnotatedWith(Service.class); //

    @ArchTest
    ArchRule noInjectionaSetter =
            noMethods().that().haveNameStartingWith("set").should().beAnnotatedWith(Autowired.class);

    @ArchTest
    ArchRule notComponent =
            noClasses().should().beAnnotatedWith(Component.class);
}