package de.propra.chicken.domain.model;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Student {

    private final long githubID;
    private int resturlaub;
    private Set<Urlaub> urlaube = new HashSet<>();
    private Set<KlausurRef> klausuren = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return githubID == student.githubID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(githubID, resturlaub, urlaube, klausuren);
    }

    @Override
    public String toString() {
        return "Student{" +
                "githubID=" + githubID +
                ", resturlaub=" + resturlaub +
                ", urlaube=" + urlaube +
                ", klausuren=" + klausuren +
                '}';
    }

    public Student(long githubID) {
        this.githubID = githubID;
        this.resturlaub = 240;
    }

    public Student(long githubID, int resturlaub) {
        this.githubID = githubID;
        this.resturlaub = resturlaub;
    }

    public long getGithubID() {
        return githubID;
    }

    public Set<KlausurRef> getKlausuren() {
        return new HashSet<>(klausuren);
    }

    public int getResturlaub() {
        return resturlaub;
    }

    public void aendereUrlaube(Set<Urlaub> gueltigeUrlaube, LocalDate datum){
        Set<Urlaub> alteUrlaube = new HashSet<>(urlaube);
        for(Urlaub u: alteUrlaube){
            if(u.getTag().compareTo(datum) == 0){
                urlaube.remove(u);
                erstatteUrlaubsdauer(u);
            }
        }
        zieheUrlaubsdauerAb(gueltigeUrlaube);
        urlaube.addAll(gueltigeUrlaube);

    }

    public void entferneUrlaub(String tag, String beginn, String end){
        Urlaub urlaub = new Urlaub(tag, beginn, end);
        urlaube.remove(urlaub);
        erstatteUrlaubsdauer(urlaub);
    }

    public void erstatteUrlaubsdauer(Urlaub veralteterUrlaub) {
        int erstatten = 0;
        erstatten += veralteterUrlaub.duration();
        this.resturlaub = resturlaub + erstatten;
    }

    public long  summeAllerUrlaube(){
        return this.urlaube.stream().map(a -> Duration.between(a.getBeginn(), a.getEnd()).toMinutes()).reduce(0L, Long::sum);
    }

    public void setResturlaub(int resturlaub) {
        this.resturlaub = resturlaub;
    }

    public void setUrlaube(Set<Urlaub> urlaube) {
        this.urlaube = new HashSet<>(urlaube);
        this.resturlaub = 240 - (int) summeAllerUrlaube();
    }

    public void setKlausuren(Set<KlausurRef> klausuren) {
        this.klausuren = new HashSet<>(klausuren);
    }

    public void addKlausur(KlausurRef klausur) {
        this.klausuren.add(klausur);
    }

    public void addUrlaube(Set<Urlaub> urlaub) {
        urlaube.addAll(urlaub);
        zieheUrlaubsdauerAb(urlaub);
    }


    public Set<Urlaub> urlaubSelberTag(LocalDate datum) {
        Set<Urlaub> uSelberTag = new HashSet<>();
        urlaube.stream().filter(a -> a.getTag().compareTo(datum) == 0).forEach(uSelberTag::add);
        return uSelberTag;
    }

    public Set<Urlaub> getUrlaube() {
        return new HashSet<>(urlaube);
    }

    public Set<Urlaub> urlaubeZusammenfuegen() {
        if (urlaube.size() == 0) return new HashSet<>(urlaube);

        Set<Urlaub> zuPruefendeUrlaube = new HashSet<>(urlaube);
        Set<Urlaub> stashUrlaube = new HashSet<>();
        Set<Urlaub> urlaubAmSelbenTag = new HashSet<>();
        boolean aenderung = true;

        while (aenderung) {
            aenderung = false;
            for (Urlaub neu : zuPruefendeUrlaube) {
                if (aenderung) break;
                stashUrlaube.addAll(zuPruefendeUrlaube);
                urlaubAmSelbenTag.addAll(urlaubSelberTag(neu.getTag()));
                for (Urlaub alt : urlaubAmSelbenTag) {
                    if (alt.getBeginn().equals(neu.getEnd())) {
                        stashUrlaube.remove(alt);
                        stashUrlaube.remove(neu);
                        stashUrlaube.add(new Urlaub(neu.getTag().toString(), neu.getBeginn().toString(), alt.getEnd().toString()));
                        aenderung = true;
                        break;
                    } else if (neu.getBeginn().equals(alt.getEnd())) {
                        stashUrlaube.remove(alt);
                        stashUrlaube.remove(neu);
                        stashUrlaube.add(new Urlaub(neu.getTag().toString(), alt.getBeginn().toString(), neu.getEnd().toString()));
                        aenderung = true;
                        break;
                    }
                }
                zuPruefendeUrlaube = stashUrlaube;
                stashUrlaube = new HashSet<>();
                urlaubAmSelbenTag = new HashSet<>();
            }
        }
        return zuPruefendeUrlaube;
    }


    public void zieheUrlaubsdauerAb(Set<Urlaub> gueltigerNeuerUrlaub) {
        int abzuziehen = 0;
        for(Urlaub urlaub : gueltigerNeuerUrlaub) {
            abzuziehen += urlaub.duration();
        }
        this.resturlaub = resturlaub - abzuziehen;
    }

    public void entferneKlausur(long lsfID){
        klausuren.remove(new KlausurRef(lsfID));
    }

    public void entferneUrlaubeAnEinemTag(LocalDate datum){
        urlaube.stream().filter(a -> a.getTag().equals(datum)).forEach(this::erstatteUrlaubsdauer);
        urlaube = urlaube.stream().filter(a -> !(a.getTag().equals(datum))).collect(Collectors.toSet());
    }

}
