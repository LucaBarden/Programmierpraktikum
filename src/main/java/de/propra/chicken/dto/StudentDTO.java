package de.propra.chicken.dto;

import de.propra.chicken.domain.model.Student;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

@Table("student")
public class StudentDTO {

    @Id
    private long ID;
    private final long githubID;
    private int resturlaub;
    private Set<UrlaubDTO> urlaub;

    public StudentDTO(Student student) {
        this.githubID = student.getGithubID();
        this.resturlaub = student.getResturlaub();
        this.urlaub = student.getUrlaube().stream().map(u -> new UrlaubDTO(u, githubID)).collect(Collectors.toSet());
    }


    public long getGithubID() {
        return githubID;
    }

    public int getResturlaub() {
        return resturlaub;
    }
}
