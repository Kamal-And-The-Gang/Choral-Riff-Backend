package fr.afpa.choral_riff.dto;

public record DescriptifRequestDto(
    String titre,
    String compositeur,
    String genre
) {}