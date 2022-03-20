package de.propra.chicken.domain.model;

import de.propra.chicken.domain.dto.StudentDTO;
import de.propra.chicken.domain.dto.UrlaubDTO;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Student {

    private final long githubID;
    private int resturlaub;
    private Set<Urlaub> urlaube = new HashSet<>();
    private Set<KlausurRef> klausuren = new HashSet<>();

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
        return klausuren;
    }

    public int getResturlaub() {
        return resturlaub;
    }

    public void setResturlaub(int resturlaub) {
        this.resturlaub = resturlaub;
    }

    public void setUrlaube(Set<Urlaub> urlaube) {
        this.urlaube = urlaube;
    }

    public void setKlausuren(Set<KlausurRef> klausuren) {
        this.klausuren = klausuren;
    }

    public void addKlausur(KlausurRef klausur) {
        this.klausuren.add(klausur);
    }

    public void addUrlaube(Set<Urlaub> urlaub) {
        urlaube.addAll(urlaub);
    }


    public Set<Urlaub> urlaubSelberTag(Urlaub urlaub) {
        Set<Urlaub> uSelberTag = new HashSet<>();
        for (Urlaub tmpUrlaub : urlaube) {
            if (tmpUrlaub.getTag().compareTo(urlaub.getTag()) == 0) {
                uSelberTag.add(tmpUrlaub);
            }
        }
        return uSelberTag;
    }

    public StudentDTO getDto() {
        return new StudentDTO(this.githubID, this.resturlaub, this.urlaube.stream().map(u -> new UrlaubDTO(u.getTag().toString(), u.getBeginn().toString(), u.getEnd().toString())).collect(Collectors.toSet()), this.klausuren);
    }

    public Set<Urlaub> ueberschneidendenUrlaubMergen() {
        if (urlaube.size() == 0) return urlaube;

        Set<Urlaub> zuPruefendeUrlaube = new HashSet<>();
        zuPruefendeUrlaube.addAll(urlaube);
        Set<Urlaub> stashUrlaube = new HashSet<>();
        boolean aenderung = true;

        while (aenderung) {
            aenderung = false;
            for (Urlaub neu : zuPruefendeUrlaube) {
                if (aenderung) break;
                stashUrlaube.addAll(zuPruefendeUrlaube);
                for (Urlaub alt : zuPruefendeUrlaube) {
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
            }
        }
        return zuPruefendeUrlaube;
    }
}
