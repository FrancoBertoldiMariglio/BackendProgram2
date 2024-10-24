package um.edu.ar.service.mapper;

import org.mapstruct.*;
import um.edu.ar.domain.Dispositivo;
import um.edu.ar.domain.Personalizacion;
import um.edu.ar.service.dto.DispositivoDTO;
import um.edu.ar.service.dto.PersonalizacionDTO;

/**
 * Mapper for the entity {@link Personalizacion} and its DTO {@link PersonalizacionDTO}.
 */
@Mapper(componentModel = "spring")
public interface PersonalizacionMapper extends EntityMapper<PersonalizacionDTO, Personalizacion> {
    @Mapping(target = "dispositivo", source = "dispositivo", qualifiedByName = "dispositivoId")
    PersonalizacionDTO toDto(Personalizacion s);

    @Named("dispositivoId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DispositivoDTO toDtoDispositivoId(Dispositivo dispositivo);
}
