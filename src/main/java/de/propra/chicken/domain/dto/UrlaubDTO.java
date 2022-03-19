package de.propra.chicken.domain.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;

@Table("urlaub")
public class UrlaubDTO {
    private LocalDate tag;
    private LocalTime beginn;
    private LocalTime end;

    public UrlaubDTO(String tag, String beginn, String end) {
        this.tag = LocalDate.parse(tag);
        this.beginn = LocalTime.parse(beginn);
        this.end = LocalTime.parse(end);
    }
}
