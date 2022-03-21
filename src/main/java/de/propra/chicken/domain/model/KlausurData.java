package de.propra.chicken.domain.model;

import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDate;
import java.time.LocalTime;

public record KlausurData( @Column("datum")LocalDate tag, @Column("beginn") LocalTime von, @Column("ende") LocalTime bis, @Column("praesenz") boolean isPraesenz) {}

