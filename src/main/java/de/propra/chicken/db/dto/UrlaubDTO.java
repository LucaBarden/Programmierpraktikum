package de.propra.chicken.db.dto;

import de.propra.chicken.domain.model.Urlaub;
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


    public UrlaubDTO(Urlaub urlaub, long github_id){
        this.tag = urlaub.getTag();
        this.beginn = urlaub.getBeginn();
        this.end = urlaub.getEnd();
        this.github_id = github_id;
    }
}
