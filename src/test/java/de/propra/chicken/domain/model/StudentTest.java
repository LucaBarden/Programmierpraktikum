package de.propra.chicken.domain.model;

import de.propra.chicken.domain.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentTest {

    @Test
    @DisplayName("zwei Urlaubsblöcke direkt hintereinander werden zu einem Block gemerged")
    void mergenZweiUrlaubsbloeckeHintereinander() {
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

    @Test
    @DisplayName("zwei Urlaubsblöcke nicht hintereinander, werden nicht verändert")
    void zweiUrlaubsbloeckeKeinMergen() {
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
    @DisplayName("drei Urlaubsblöcke direkt nacheinander werden zu einem Block gemerged")
    void dreiUrlaubsbloeckeMergen() {
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub("1000-01-01", "09:00", "10:00");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "10:00", "11:00");
        Urlaub urlaub3 = new Urlaub("1000-01-01", "11:00", "12:00");
        Set<Urlaub> urlaube = new HashSet<>();
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        urlaube.add(urlaub3);
        student.addUrlaube(urlaube);
        Set<Urlaub> gueltigeUrlaube;

        gueltigeUrlaube = student.ueberschneidendenUrlaubMergen();
        Urlaub result = new Urlaub("1000-01-01", "09:00", "12:00");

        assertThat(gueltigeUrlaube).hasSize(1);
        assertThat(gueltigeUrlaube).contains(result);
    }

    @Test
    @DisplayName("drei Urlaubsblöcke nicht hintereinander, werden nicht verändert")
    void dreiUrlaubsbloeckeKeinMergen() {
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub("1000-01-01", "08:30", "09:30");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "09:45", "10:00");
        Urlaub urlaub3 = new Urlaub("1000-01-01", "11:00", "11:30");
        Set<Urlaub> urlaube = new HashSet<>();
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

    @Test
    @DisplayName("je zwei Urlaubsblöcke direkt nacheinander werden zu je einem Block gemerged")
    void vierUrlaubsbloeckeZweiMergen() {
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub("1000-01-01", "08:30", "09:00");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "09:00", "09:30");
        Urlaub urlaub3 = new Urlaub("1000-01-01", "11:00", "11:30");
        Urlaub urlaub4 = new Urlaub("1000-01-01", "11:30", "12:00");
        Set<Urlaub> urlaube = new HashSet<>();
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        urlaube.add(urlaub3);
        urlaube.add(urlaub4);
        student.addUrlaube(urlaube);
        Set<Urlaub> gueltigeUrlaube;

        gueltigeUrlaube = student.ueberschneidendenUrlaubMergen();
        Urlaub result = new Urlaub("1000-01-01", "08:30", "09:30");
        Urlaub result2 = new Urlaub("1000-01-01", "11:00", "12:00");

        assertThat(gueltigeUrlaube).hasSize(2);
        assertThat(gueltigeUrlaube).contains(result, result2);
    }

    @Test
    @DisplayName("Ein Student hat zwei Urlaube, einer ist zur gleichen Zeit wie eine Klausur(also der uebergebene Tag), wird also herausgelöscht")
    void aendereUrlaube1Test(){
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub("2022-02-02", "08:30", "10:00");
        Urlaub urlaub2 = new Urlaub("2022-03-03", "08:30", "09:00");
        Set<Urlaub> alteUrlaube = new HashSet<>();
        alteUrlaube.add(urlaub1);
        alteUrlaube.add(urlaub2);
        student.setUrlaube(alteUrlaube);
        student.zieheUrlaubsdauerAb(alteUrlaube);

        student.aendereUrlaube(new HashSet<>(), LocalDate.of(2022,2,2));

        assertThat(student.getResturlaub()).isEqualTo(210);
        assertThat(student.getUrlaube()).contains(urlaub2);
        assertThat(student.getUrlaube()).hasSize(1);
    }
    @Test
    @DisplayName("Ein Student hat zwei Urlaube, einer ist zur gleichen Zeit wie eine Klausur(also der uebergebene Tag), wird also herausgelöscht")
    void aendereUrlaube2Test(){
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub("2022-02-02", "08:30", "10:00"); //1.5
        Urlaub urlaub2 = new Urlaub("2022-02-02","11:30", "12:30"); //1
        Urlaub urlaub3 = new Urlaub("2022-03-03", "08:30", "10:00"); //1.5
        Urlaub gueltigeUrlaub = new Urlaub("2022-02-02", "08:30", "09:00");
        Set<Urlaub> alteUrlaube = new HashSet<>();
        alteUrlaube.add(urlaub1);
        alteUrlaube.add(urlaub2);
        alteUrlaube.add(urlaub3);
        Set<Urlaub> gueltigeUrlaube = new HashSet<>();
        gueltigeUrlaube.add(gueltigeUrlaub);
        student.setUrlaube(alteUrlaube);
        student.zieheUrlaubsdauerAb(alteUrlaube);
        System.out.println(student.toString());
        student.aendereUrlaube(gueltigeUrlaube, LocalDate.of(2022,2,2));

        assertThat(student.getResturlaub()).isEqualTo(120);
        assertThat(student.getUrlaube()).contains(urlaub3, gueltigeUrlaub);
        assertThat(student.getUrlaube()).hasSize(2);
    }

    @Test
    @DisplayName("urlaubSelberTag wird richtig berechnet")
    void urlaubSelberTag() {
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub("2022-03-10", "08:45", "09:15"); //selber Tag
        Urlaub urlaub2 = new Urlaub("2022-03-10", "10:00", "10:15"); //selber Tag
        Urlaub urlaub3 = new Urlaub("2022-03-12", "09:00", "09:15");
        Urlaub urlaub4 = new Urlaub("2022-03-10", "12:00", "12:30"); //selber Tag
        Urlaub urlaub5 = new Urlaub("2022-03-11", "10:00", "11:30");
        Urlaub urlaub6 = new Urlaub("2022-03-14", "11:30", "12:30");
        Set<Urlaub> urlaube = Set.of(urlaub1, urlaub2, urlaub3, urlaub4, urlaub5, urlaub6);
        student.addUrlaube(urlaube);

        Set<Urlaub> selberTag = student.urlaubSelberTag(urlaub1.getTag());

        assertThat(selberTag).hasSize(3);
        assertThat(selberTag).contains(urlaub1);
        assertThat(selberTag).contains(urlaub2);
        assertThat(selberTag).contains(urlaub4);
    }

    @Test
    @DisplayName("summeAllerUrlaube wird richtig berechnet")
    void summeUrlaube() {
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub("2022-03-10", "08:45", "09:15");
        Urlaub urlaub2 = new Urlaub("2022-03-10", "10:00", "10:15");
        Urlaub urlaub3 = new Urlaub("2022-03-12", "09:00", "09:15");
        Urlaub urlaub4 = new Urlaub("2022-03-11", "10:00", "11:30");
        Urlaub urlaub5 = new Urlaub("2022-03-14", "11:30", "12:30");
        Set<Urlaub> urlaube = Set.of(urlaub1, urlaub2, urlaub3, urlaub4, urlaub5);
        student.addUrlaube(urlaube);

        long summe = student.summeAllerUrlaube();

        assertThat(summe).isEqualTo(210);
    }

    @Test
    @DisplayName("zieheUrlaubsdauerAb")
    void reduziereResturlaub() {
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub("2022-03-10", "08:45", "09:15");
        Urlaub urlaub2 = new Urlaub("2022-03-10", "10:00", "10:15");
        Urlaub urlaub3 = new Urlaub("2022-03-12", "09:00", "09:15");
        Urlaub urlaub4 = new Urlaub("2022-03-11", "10:00", "11:30");
        Urlaub urlaub5 = new Urlaub("2022-03-14", "11:30", "12:30");
        Set<Urlaub> gueltigeUrlaube = Set.of(urlaub1, urlaub2, urlaub3, urlaub4, urlaub5);

        student.zieheUrlaubsdauerAb(gueltigeUrlaube);
        int resturlaub = student.getResturlaub();

        assertThat(resturlaub).isEqualTo(30);
    }
}
