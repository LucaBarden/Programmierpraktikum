package de.propra.chicken.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentTest {

    @Test
    @DisplayName("Zwei Urlaubsbloecke, die direkt hintereinander liegen, werden zu einem Block gemerged")
    void mergenZweiUrlaubsbloeckeHintereinander() {
        Student student = new Student(123);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub("1000-01-01", "10:00", "11:00");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "11:00", "12:00");
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        student.addUrlaube(urlaube);
        Set<Urlaub> gueltigeUrlaube;

        gueltigeUrlaube = student.urlaubeZusammenfuegen();
        Urlaub result = new Urlaub("1000-01-01", "10:00", "12:00");

        assertThat(gueltigeUrlaube).hasSize(1);
        assertThat(gueltigeUrlaube).contains(result);
    }

    @Test
    @DisplayName("Zwei Urlaubsbloecke, die nicht hintereinander liegen, werden nicht veraendert")
    void zweiUrlaubsbloeckeKeinMergen() {
        Student student = new Student(123);
        Set<Urlaub> urlaube = new HashSet<>();
        Urlaub urlaub1 = new Urlaub("1000-01-01", "10:00", "11:00");
        Urlaub urlaub2 = new Urlaub("1000-01-01", "11:30", "12:00");
        urlaube.add(urlaub1);
        urlaube.add(urlaub2);
        student.addUrlaube(urlaube);
        Set<Urlaub> gueltigeUrlaube;

        gueltigeUrlaube = student.urlaubeZusammenfuegen();
        Urlaub result = new Urlaub("1000-01-01", "10:00", "11:00");
        Urlaub result2 = new Urlaub("1000-01-01", "11:30", "12:00");

        assertThat(gueltigeUrlaube).hasSize(2);
        assertThat(gueltigeUrlaube).contains(result, result2);
    }

    @Test
    @DisplayName("Drei Urlaubsbloecke,die direkt nacheinander liegen, werden zu einem Block gemerged")
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

        gueltigeUrlaube = student.urlaubeZusammenfuegen();
        Urlaub result = new Urlaub("1000-01-01", "09:00", "12:00");

        assertThat(gueltigeUrlaube).hasSize(1);
        assertThat(gueltigeUrlaube).contains(result);
    }

    @Test
    @DisplayName("Drei Urlaubsbloecke,die nicht hintereinander liegen, werden nicht gemerged")
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

        gueltigeUrlaube = student.urlaubeZusammenfuegen();
        Urlaub result = new Urlaub("1000-01-01", "08:30", "09:30");
        Urlaub result2 = new Urlaub("1000-01-01", "09:45", "10:00");
        Urlaub result3 = new Urlaub("1000-01-01", "11:00", "11:30");

        assertThat(gueltigeUrlaube).hasSize(3);
        assertThat(gueltigeUrlaube).contains(result, result2, result3);
    }

    @Test
    @DisplayName("Je zwei Urlaubsbloecke direkt nacheinander werden zu je einem Block gemerged")
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

        gueltigeUrlaube = student.urlaubeZusammenfuegen();
        Urlaub result = new Urlaub("1000-01-01", "08:30", "09:30");
        Urlaub result2 = new Urlaub("1000-01-01", "11:00", "12:00");

        assertThat(gueltigeUrlaube).hasSize(2);
        assertThat(gueltigeUrlaube).contains(result, result2);
    }

    @Test
    @DisplayName("Ein Student hat zwei Urlaube, einer ist zur gleichen Zeit wie eine Klausur(also der uebergebene Tag), wird also erstattet")
    void aendereUrlaube1Test(){
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub("2022-02-02", "08:30", "10:00");
        Urlaub urlaub2 = new Urlaub("2022-03-03", "08:30", "09:00");
        Set<Urlaub> alteUrlaube = new HashSet<>();
        alteUrlaube.add(urlaub1);
        alteUrlaube.add(urlaub2);
        student.setUrlaube(alteUrlaube);

        student.aendereUrlaube(new HashSet<>(), LocalDate.of(2022,2,2));

        assertThat(student.getResturlaub()).isEqualTo(210);
        assertThat(student.getUrlaube()).contains(urlaub2);
        assertThat(student.getUrlaube()).hasSize(1);
    }
    @Test
    @DisplayName("Ein Student hat drei Urlaube, zwei ueberschneiden sich mit einer Klausur(also der uebergebene Tag)," +
            "also wird das Ende eines Urlaubs und der andere Urlaub komplett erstattet")
    void aendereUrlaube2Test(){
        Student student = new Student(123);
        Urlaub urlaub1 = new Urlaub("2022-02-02", "08:30", "10:00"); //1.5
        Urlaub urlaub2 = new Urlaub("2022-02-02","11:30", "12:30"); //1
        Urlaub urlaub3 = new Urlaub("2022-03-03", "08:30", "10:00"); //1.5
        Urlaub gueltigerUrlaub = new Urlaub("2022-02-02", "08:30", "09:00");
        Set<Urlaub> alteUrlaube = new HashSet<>();
        alteUrlaube.add(urlaub1);
        alteUrlaube.add(urlaub2);
        alteUrlaube.add(urlaub3);
        Set<Urlaub> gueltigeUrlaube = new HashSet<>();
        gueltigeUrlaube.add(gueltigerUrlaub);
        student.setUrlaube(alteUrlaube);
        student.aendereUrlaube(gueltigeUrlaube, LocalDate.of(2022,2,2));

        assertThat(student.getResturlaub()).isEqualTo(120);
        assertThat(student.getUrlaube()).contains(urlaub3, gueltigerUrlaub);
        assertThat(student.getUrlaube()).hasSize(2);
    }

    @Test
    @DisplayName("urlaubSelberTag wird richtig berechnet, bei mehereren gleichen Tagen")
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
    @DisplayName("Ein Urlaub wird Korrekt vom Resturlaub abgezogen")
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

    @Test
    @DisplayName("Entfernt einzigen Urlaub dieses Tages ")
    void entferneUrlaubTest1(){
        Student student = new Student(123 );
        Urlaub urlaub = new Urlaub("2022-02-02", "08:30", "10:00");
        Set<Urlaub> urlaube = new HashSet<>();
        urlaube.add(urlaub);
        student.setUrlaube(urlaube);

        student.entferneUrlaub("2022-02-02", "08:30", "10:00");

        assertThat(student.getUrlaube()).hasSize(0);
        assertThat(student.getResturlaub()).isEqualTo(240);
    }
    @Test
    @DisplayName("Entfernt einzigen Urlaub dieses Tages mit mehreren Urlauben  ")
    void entferneUrlaubTest2(){
        Student student = new Student(123 );
        Set<Urlaub> urlaube = new HashSet<>();
        urlaube.add(new Urlaub("2022-02-02", "08:30", "10:00"));
        urlaube.add(new Urlaub("2022-02-03", "08:30", "09:30"));
        urlaube.add(new Urlaub("2022-02-03", "11:30", "12:30"));

        student.setUrlaube(urlaube);

        student.entferneUrlaub("2022-02-02", "08:30", "10:00");

        assertThat(student.getUrlaube()).hasSize(2);
        assertThat(student.getResturlaub()).isEqualTo(120);
    }

    @Test
    @DisplayName("Entfernt den ganzen Tag bei mehreren Urlauben am Tag")
    void entferneUrlaubAnEinemTagTest1(){
        Student student = new Student(123 );
        Set<Urlaub> urlaube = new HashSet<>();
        urlaube.add(new Urlaub("2022-02-02", "08:30", "10:00"));
        urlaube.add(new Urlaub("2022-02-02", "12:00", "12:30"));
        urlaube.add(new Urlaub("2022-02-03", "11:30", "12:30"));

        student.setUrlaube(urlaube);

        student.entferneUrlaubeAnEinemTag(LocalDate.of(2022,2,2));

        assertThat(student.getUrlaube()).hasSize(1);
        assertThat(student.getResturlaub()).isEqualTo(180);
    }

    @Test
    @DisplayName("Entfernt eine Klausur aus der Liste der angemeldeten Klausuren")
    void entferneKlausur(){
        Student student = new Student(123 );
        Set<KlausurRef> klausurRefs = new HashSet<>();
        KlausurRef ref1 = new KlausurRef(123);
        KlausurRef ref2 = new KlausurRef(456);
        KlausurRef ref3 = new KlausurRef(789);
        klausurRefs.add(ref1);
        klausurRefs.add(ref2);
        klausurRefs.add(ref3);
        student.setKlausuren(klausurRefs);

        student.entferneKlausur(123);

        assertThat(student.getKlausuren()).hasSize(2);
        assertThat(student.getKlausuren()).contains(ref2);
        assertThat(student.getKlausuren()).contains(ref3);
    }
}
