package de.propra.chicken.domain.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Table("student")
public class StudentDTO {

    @Id
    private long ID;
    private final long githubID;
    private int resturlaub;
    private Set<UrlaubDTO> urlaub;

    public StudentDTO(Set<UrlaubDTO> urlaube, int resturlaub, long githubID) {
        this.urlaub = urlaube;
        this.resturlaub = resturlaub;
        this.githubID = githubID;
    }


    public long getGithubID() {
        return githubID;
    }

    public int getResturlaub() {
        return resturlaub;
    }
}
