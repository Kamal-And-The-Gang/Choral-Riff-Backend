package main.java.fr.afpa.choral_riff.mapper;


@Mapper(componentModel = "spring")
public interface MorceauMapper {

    @Mapping(source = "ensemble.idEnsemble", target = "ensembleId")
    @Mapping(source = "createur.id", target = "createurId")
    MorceauDto toDto(Morceau morceau);

    @Mapping(target = "ensemble", ignore = true)
    @Mapping(target = "createur", ignore = true)
    @Mapping(target = "documents", ignore = true)
    Morceau toEntity(MorceauDto dto);
}
