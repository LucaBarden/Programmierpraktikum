package de.propra.chicken.domain.model;

import org.springframework.data.relational.core.mapping.Table;


public class Student{

    //private String handle;
    private int restUrlaub;

    public int getRestUrlaub() {
        return restUrlaub;
    }

    public long getGithubID() {
        return githubID;
    }

    private long githubID;

    public Student(int restUrlaub, long githubID) {
        this.restUrlaub = restUrlaub;
        this.githubID = githubID;
    }

    public Student(long githubID) {
        this.githubID = githubID;
        this.restUrlaub = 240;
    }
}
