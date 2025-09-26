package fr.afpa.choral_riff.dto;

import java.time.LocalDate;

public record EnsembleDto(
    Long ensembleId,
    String nom,
    String description,
    LocalDate dateCreation
) {}