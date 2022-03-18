package de.propra.chicken.dto;

import org.springframework.data.annotation.Id;

public class StudentDTO {

    @Id
    private final long githubID;
    private int resturlaub;

    public StudentDTO(long githubID, int resturlaub) {
        this.githubID = githubID;
        this.resturlaub = resturlaub;
    }

    public long getGithubID() {
        return githubID;
    }

    public int getResturlaub() {
        return resturlaub;
    }
}
