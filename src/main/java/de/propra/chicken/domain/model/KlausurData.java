package de.propra.chicken.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record KlausurData(LocalDate tag, LocalTime von, LocalTime bis, boolean isPraesenz) {}

