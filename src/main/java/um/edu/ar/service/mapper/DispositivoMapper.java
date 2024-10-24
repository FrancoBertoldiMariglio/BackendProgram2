package um.edu.ar.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import um.edu.ar.domain.Adicional;
import um.edu.ar.domain.Dispositivo;
import um.edu.ar.service.dto.*;

/**
 * Mapper for the entity {@link Dispositivo} and its DTO {@link MapperDispositivoDTO}.
 */
@Mapper(componentModel = "spring")
public interface DispositivoMapper extends EntityMapper<MapperDispositivoDTO, Dispositivo> {
    @Mapping(target = "caracteristicas", source = "caracteristicas")
    @Mapping(target = "personalizaciones", source = "personalizaciones")
    @Mapping(target = "adicionales", source = "adicionales")
    @Mapping(target = "removeAdicionales", ignore = true)
    Dispositivo toEntity(DispositivoDTO dispositivoDTO);

    @Mapping(target = "caracteristicas", source = "caracteristicas")
    @Mapping(target = "personalizaciones", source = "personalizaciones")
    @Mapping(target = "adicionales", source = "adicionales")
    default MapperDispositivoDTO toDto(Dispositivo dispositivo) {
        try {
            MapperDispositivoDTO dto = new MapperDispositivoDTO();
            dto.setId(dispositivo.getId());
            dto.setCodigo(dispositivo.getCodigo());
            dto.setNombre(dispositivo.getNombre());
            dto.setDescripcion(dispositivo.getDescripcion());
            dto.setPrecioBase(dispositivo.getPrecioBase());
            dto.setMoneda(dispositivo.getMoneda());

            dto.setCaracteristicas(
                dispositivo
                    .getCaracteristicas()
                    .stream()
                    .map(caracteristica -> {
                        CaracteristicaDTO caracteristicaDTO = new CaracteristicaDTO();
                        caracteristicaDTO.setId(caracteristica.getId());
                        caracteristicaDTO.setNombre(caracteristica.getNombre());
                        caracteristicaDTO.setDescripcion(caracteristica.getDescripcion());
                        return caracteristicaDTO;
                    })
                    .collect(Collectors.toSet())
            );

            dto.setPersonalizaciones(
                dispositivo
                    .getPersonalizaciones()
                    .stream()
                    .map(personalizacion -> {
                        MapperPersonalizacionDTO mapperPersonalizacionDTO = new MapperPersonalizacionDTO();
                        mapperPersonalizacionDTO.setId(personalizacion.getId());
                        mapperPersonalizacionDTO.setNombre(personalizacion.getNombre());
                        mapperPersonalizacionDTO.setDescripcion(personalizacion.getDescripcion());

                        mapperPersonalizacionDTO.setOpciones(
                            personalizacion
                                .getOpciones()
                                .stream()
                                .map(opcion -> {
                                    OpcionDTO opcionDTO = new OpcionDTO();
                                    opcionDTO.setId(opcion.getId());
                                    opcionDTO.setCodigo(opcion.getCodigo());
                                    opcionDTO.setNombre(opcion.getNombre());
                                    opcionDTO.setDescripcion(opcion.getDescripcion());
                                    opcionDTO.setPrecioAdicional(opcion.getPrecioAdicional());
                                    return opcionDTO;
                                })
                                .collect(Collectors.toSet())
                        );
                        return mapperPersonalizacionDTO;
                    })
                    .collect(Collectors.toSet())
            );

            dto.setAdicionales(
                dispositivo
                    .getAdicionales()
                    .stream()
                    .map(adicional -> {
                        AdicionalDTO adicionalDTO = new AdicionalDTO();
                        adicionalDTO.setId(adicional.getId());
                        adicionalDTO.setNombre(adicional.getNombre());
                        adicionalDTO.setDescripcion(adicional.getDescripcion());
                        adicionalDTO.setPrecio(adicional.getPrecio());
                        adicionalDTO.setPrecioGratis(adicional.getPrecioGratis());
                        return adicionalDTO;
                    })
                    .collect(Collectors.toSet())
            );

            return dto;
        } catch (Exception e) {
            System.out.println("Error mapping Dispositivo to MapperDispositivoDTO");
            return null;
        }
    }

    @Named("adicionalId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AdicionalDTO toDtoAdicionalId(Adicional adicional);

    @Named("adicionalIdSet")
    default Set<AdicionalDTO> toDtoAdicionalIdSet(Set<Adicional> adicional) {
        return adicional.stream().map(this::toDtoAdicionalId).collect(Collectors.toSet());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Dispositivo partialUpdate(@MappingTarget Dispositivo dispositivo, DispositivoDTO dispositivoDTO);
}
