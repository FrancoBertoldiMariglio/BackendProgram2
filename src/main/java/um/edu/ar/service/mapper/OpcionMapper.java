package um.edu.ar.service.mapper;

import org.mapstruct.*;
import um.edu.ar.domain.Opcion;
import um.edu.ar.domain.Personalizacion;
import um.edu.ar.service.dto.OpcionDTO;
import um.edu.ar.service.dto.PersonalizacionDTO;

/**
 * Mapper for the entity {@link Opcion} and its DTO {@link OpcionDTO}.
 */
@Mapper(componentModel = "spring")
public interface OpcionMapper extends EntityMapper<OpcionDTO, Opcion> {
    @Mapping(target = "personalizacion", source = "personalizacion", qualifiedByName = "personalizacionId")
    OpcionDTO toDto(Opcion s);

    @Named("personalizacionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PersonalizacionDTO toDtoPersonalizacionId(Personalizacion personalizacion);
}
