package de.propra.chicken.dto;

import de.propra.chicken.domain.model.Urlaub;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalTime;

public class UrlaubDTO {
    private LocalDate tag;
    private LocalTime beginn;
    private LocalTime end;
    @Id
    private int ID;
    private long github_id;

    public UrlaubDTO(String tag, String beginn, String end, long github_id) {
        this.tag = LocalDate.parse(tag);
        this.beginn = LocalTime.parse(beginn);
        this.end = LocalTime.parse(end);
        this.github_id = github_id;
    }
    public UrlaubDTO(Urlaub urlaub, long github_id){
        this.tag = urlaub.getTag();
        this.beginn = urlaub.getBeginn();
        this.end = urlaub.getEnd();
        this.github_id = github_id;
    }
}
