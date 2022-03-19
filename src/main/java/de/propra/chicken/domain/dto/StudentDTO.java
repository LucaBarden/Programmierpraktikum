package de.propra.chicken.domain.dto;
import de.propra.chicken.domain.model.KlausurRef;
import de.propra.chicken.domain.model.Urlaub;
import org.springframework.data.annotation.Id;

import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Table("student")
public class StudentDTO implements Persistable {

    @Id
    @Column("student_id")
    private long githubID;
    private int resturlaub;
    private Set<UrlaubDTO> urlaube;
    private Set<KlausurRef> klausuren = new HashSet<>();

    @Transient
    private boolean isNew = true;

    public StudentDTO(long githubID,  int resturlaub,  Set<UrlaubDTO> urlaube, Set<KlausurRef> refs) {
        this.klausuren = refs;
        this.urlaube = urlaube;
        this.resturlaub = resturlaub;
        this.githubID = githubID;
    }

    public StudentDTO() {

    }


    public long getGithubID() {
        return githubID;
    }

    public int getResturlaub() {
        return resturlaub;
    }

    @Override
    public String toString() {
        return "StudentDTO{" +
                "githubID=" + githubID +
                ", resturlaub=" + resturlaub +
                ", urlaube=" + urlaube +
                ", klausuren=" + klausuren +
                ", isNew=" + isNew +
                '}';
    }

    public Set<Urlaub> getUrlaube() {
        return urlaube.stream().map(u -> u.getUrlaub()).collect(Collectors.toSet());
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public Long getId() {
        return this.githubID;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
