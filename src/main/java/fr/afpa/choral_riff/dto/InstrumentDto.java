package fr.afpa.choral_riff.dto;

public record InstrumentDto(
    Long id,
    String nom,
    Long ensembleId  // juste l'id de l'ensemble
) {}


