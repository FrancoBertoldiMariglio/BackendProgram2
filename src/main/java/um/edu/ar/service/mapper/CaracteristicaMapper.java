package um.edu.ar.service.mapper;

import org.mapstruct.*;
import um.edu.ar.domain.Caracteristica;
import um.edu.ar.domain.Dispositivo;
import um.edu.ar.service.dto.CaracteristicaDTO;
import um.edu.ar.service.dto.DispositivoDTO;

/**
 * Mapper for the entity {@link Caracteristica} and its DTO {@link CaracteristicaDTO}.
 */
@Mapper(componentModel = "spring")
public interface CaracteristicaMapper extends EntityMapper<CaracteristicaDTO, Caracteristica> {
    @Mapping(target = "dispositivo", source = "dispositivo", qualifiedByName = "dispositivoId")
    CaracteristicaDTO toDto(Caracteristica s);

    @Named("dispositivoId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DispositivoDTO toDtoDispositivoId(Dispositivo dispositivo);
}
