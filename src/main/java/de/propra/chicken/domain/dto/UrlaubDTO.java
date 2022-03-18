package de.propra.chicken.domain.dto;

import de.propra.chicken.domain.model.Urlaub;
import org.apache.tomcat.jni.Local;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;

@Table("urlaub")
public class UrlaubDTO {

    private LocalDate tag;
    private LocalTime beginn;
    private LocalTime end;
    @Id
    private int ID;
    private long github_id;


    public UrlaubDTO(LocalDate tag, LocalTime beginn, LocalTime end, long github_id){
        this.tag = tag;
        this.beginn = beginn;
        this.end = end;
        this.github_id = github_id;
    }
}
