package de.propra.chicken.db;

import de.propra.chicken.domain.model.Urlaub;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;

@Table("URLAUB")
public class UrlaubDTO {
    private LocalDate tag;
    private LocalTime beginn;
    private LocalTime ende;

    public UrlaubDTO(String tag, String beginn, String ende) {
        this.tag = LocalDate.parse(tag);
        this.beginn = LocalTime.parse(beginn);
        this.ende = LocalTime.parse(ende);
    }
    public UrlaubDTO() {

    }

    public Urlaub getUrlaub() {
        return new Urlaub(tag.toString(), beginn.toString(), ende.toString());
    }
}
