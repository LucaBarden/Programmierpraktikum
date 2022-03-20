package de.propra.chicken.domain.model;

import de.propra.chicken.domain.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentTest {

    /*@Test
    @DisplayName("zwei überschneidende Urlaubsblöcke werden gemerged")
    void urlaubsanmeldungZweiUrlaubsbloeckeUeberschneidend() {
        Student student = new Student(123);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub("1000-01-01", "10:00", "11:00");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "10:30", "11:30");
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        student.addUrlaube(urlaube);
        Set<Urlaub> gueltigeUrlaube;

        gueltigeUrlaube = student.ueberschneidendenUrlaubMergen();
        Urlaub result = new Urlaub("1000-01-01", "10:00", "11:30");

        assertThat(gueltigeUrlaube).hasSize(1);
        assertThat(gueltigeUrlaube).contains(result);
    }*/

    @Test
    @DisplayName("zwei Urlaubsblöcke direkt hintereinander werden gemerged")
    void urlaubsanmeldungZweiUrlaubsbloeckeHintereinander() {
        Student student = new Student(123);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub("1000-01-01", "10:00", "11:00");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "11:00", "12:00");
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        student.addUrlaube(urlaube);
        Set<Urlaub> gueltigeUrlaube;

        gueltigeUrlaube = student.ueberschneidendenUrlaubMergen();
        Urlaub result = new Urlaub("1000-01-01", "10:00", "12:00");

        assertThat(gueltigeUrlaube).hasSize(1);
        assertThat(gueltigeUrlaube).contains(result);
    }

    /*@Test
    @DisplayName("zwei Urlaubsblöcke überschneiden sich, der eine enthält den anderen komplett")
    void urlaubsanmeldungZweiUrlaubsbloeckeEinerLaenger() {
        Student student = new Student(123);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub("1000-01-01", "10:00", "11:00");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "10:00", "12:00");
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        student.addUrlaube(urlaube);
        Set<Urlaub> gueltigeUrlaube;

        gueltigeUrlaube = student.ueberschneidendenUrlaubMergen();
        Urlaub result = new Urlaub("1000-01-01", "10:00", "12:00");

        assertThat(gueltigeUrlaube).hasSize(1);
        assertThat(gueltigeUrlaube).contains(result);
    }*/

    @Test
    @DisplayName("zwei Urlaubsblöcke überschneiden sich nicht")
    void urlaubsanmeldungZweiUrlaubsbloeckeKeineUeberschneidung() {
        Student student = new Student(123);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub("1000-01-01", "10:00", "11:00");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "11:30", "12:00");
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        student.addUrlaube(urlaube);
        Set<Urlaub> gueltigeUrlaube;

        gueltigeUrlaube = student.ueberschneidendenUrlaubMergen();
        Urlaub result = new Urlaub("1000-01-01", "10:00", "11:00");
        Urlaub result2 = new Urlaub("1000-01-01", "11:30", "12:00");

        assertThat(gueltigeUrlaube).hasSize(2);
        assertThat(gueltigeUrlaube).contains(result, result2);
    }

    @Test
    @DisplayName("drei Urlaubsblöcke direkt nacheinander zu einem Block")
    void urlaubsanmeldungDreiUrlaubsbloeckeNacheinander() {
        Student student = new Student(123);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub("1000-01-01", "08:30", "08:45");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "08:45", "09:30");
        Urlaub urlaub3 = new Urlaub("1000-01-01", "09:30", "10:00");
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        urlaube.add(urlaub3);
        student.addUrlaube(urlaube);
        Set<Urlaub> gueltigeUrlaube;

        gueltigeUrlaube = student.ueberschneidendenUrlaubMergen();
        Urlaub result = new Urlaub("1000-01-01", "08:30", "10:00");

        assertThat(gueltigeUrlaube).hasSize(1);
        assertThat(gueltigeUrlaube).contains(result);
    }

    @Test
    @DisplayName("drei Urlaubsblöcke, keine Überschneidungen")
    void urlaubsanmeldungDreiUrlaubsbloeckeKeineUeberschneidung() {
        Student student = new Student(123);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub("1000-01-01", "08:30", "09:30");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "09:45", "10:00");
        Urlaub urlaub3 = new Urlaub("1000-01-01", "11:00", "11:30");
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        urlaube.add(urlaub3);
        student.addUrlaube(urlaube);
        Set<Urlaub> gueltigeUrlaube;

        gueltigeUrlaube = student.ueberschneidendenUrlaubMergen();
        Urlaub result = new Urlaub("1000-01-01", "08:30", "09:30");
        Urlaub result2 = new Urlaub("1000-01-01", "09:45", "10:00");
        Urlaub result3 = new Urlaub("1000-01-01", "11:00", "11:30");

        assertThat(gueltigeUrlaube).hasSize(3);
        assertThat(gueltigeUrlaube).contains(result, result2, result3);
    }

    /*@Test
    @DisplayName("vier Urlaubsblöcke, je zwei überschneiden sich")
    void urlaubsanmeldungVierUrlaubsbloeckeZweiUeberschneidungen() {
        Student student = new Student(123);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub("1000-01-01", "08:30", "09:30");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "09:00", "10:00");
        Urlaub urlaub3 = new Urlaub("1000-01-01", "11:00", "11:30");
        Urlaub urlaub4 = new Urlaub("1000-01-01", "11:15", "12:00");
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        urlaube.add(urlaub3);
        urlaube.add(urlaub4);
        student.addUrlaube(urlaube);
        Set<Urlaub> gueltigeUrlaube;

        gueltigeUrlaube = student.ueberschneidendenUrlaubMergen();
        Urlaub result = new Urlaub("1000-01-01", "08:30", "10:00");
        Urlaub result2 = new Urlaub("1000-01-01", "11:00", "12:00");

        assertThat(gueltigeUrlaube).hasSize(2);
        assertThat(gueltigeUrlaube).contains(result, result2);
    }*/
}
