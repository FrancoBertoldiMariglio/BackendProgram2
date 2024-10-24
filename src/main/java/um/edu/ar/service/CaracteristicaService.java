package um.edu.ar.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.ar.domain.Caracteristica;
import um.edu.ar.repository.CaracteristicaRepository;
import um.edu.ar.service.dto.CaracteristicaDTO;
import um.edu.ar.service.mapper.CaracteristicaMapper;

/**
 * Service Implementation for managing {@link um.edu.ar.domain.Caracteristica}.
 */
@Service
@Transactional
public class CaracteristicaService {

    private static final Logger LOG = LoggerFactory.getLogger(CaracteristicaService.class);

    private final CaracteristicaRepository caracteristicaRepository;

    private final CaracteristicaMapper caracteristicaMapper;

    public CaracteristicaService(CaracteristicaRepository caracteristicaRepository, CaracteristicaMapper caracteristicaMapper) {
        this.caracteristicaRepository = caracteristicaRepository;
        this.caracteristicaMapper = caracteristicaMapper;
    }

    /**
     * Save a caracteristica.
     *
     * @param caracteristicaDTO the entity to save.
     * @return the persisted entity.
     */
    public CaracteristicaDTO save(CaracteristicaDTO caracteristicaDTO) {
        LOG.debug("Request to save Caracteristica : {}", caracteristicaDTO);
        Caracteristica caracteristica = caracteristicaMapper.toEntity(caracteristicaDTO);
        caracteristica = caracteristicaRepository.save(caracteristica);
        return caracteristicaMapper.toDto(caracteristica);
    }

    /**
     * Update a caracteristica.
     *
     * @param caracteristicaDTO the entity to save.
     * @return the persisted entity.
     */
    public CaracteristicaDTO update(CaracteristicaDTO caracteristicaDTO) {
        LOG.debug("Request to update Caracteristica : {}", caracteristicaDTO);
        Caracteristica caracteristica = caracteristicaMapper.toEntity(caracteristicaDTO);
        caracteristica = caracteristicaRepository.save(caracteristica);
        return caracteristicaMapper.toDto(caracteristica);
    }

    /**
     * Partially update a caracteristica.
     *
     * @param caracteristicaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CaracteristicaDTO> partialUpdate(CaracteristicaDTO caracteristicaDTO) {
        LOG.debug("Request to partially update Caracteristica : {}", caracteristicaDTO);

        return caracteristicaRepository
            .findById(caracteristicaDTO.getId())
            .map(existingCaracteristica -> {
                caracteristicaMapper.partialUpdate(existingCaracteristica, caracteristicaDTO);

                return existingCaracteristica;
            })
            .map(caracteristicaRepository::save)
            .map(caracteristicaMapper::toDto);
    }

    /**
     * Get all the caracteristicas.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CaracteristicaDTO> findAll() {
        LOG.debug("Request to get all Caracteristicas");
        return caracteristicaRepository
            .findAll()
            .stream()
            .map(caracteristicaMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one caracteristica by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CaracteristicaDTO> findOne(Long id) {
        LOG.debug("Request to get Caracteristica : {}", id);
        return caracteristicaRepository.findById(id).map(caracteristicaMapper::toDto);
    }

    /**
     * Delete the caracteristica by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Caracteristica : {}", id);
        caracteristicaRepository.deleteById(id);
    }
}
