package um.edu.ar.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.ar.domain.Dispositivo;
import um.edu.ar.repository.DispositivoRepository;
import um.edu.ar.service.dto.DispositivoDTO;
import um.edu.ar.service.dto.MapperDispositivoDTO;
import um.edu.ar.service.mapper.DispositivoMapper;

/**
 * Service Implementation for managing {@link um.edu.ar.domain.Dispositivo}.
 */
@Service
@Transactional
public class DispositivoService {

    private static final Logger LOG = LoggerFactory.getLogger(DispositivoService.class);

    private final DispositivoRepository dispositivoRepository;

    private final DispositivoMapper dispositivoMapper;

    public DispositivoService(DispositivoRepository dispositivoRepository, DispositivoMapper dispositivoMapper) {
        this.dispositivoRepository = dispositivoRepository;
        this.dispositivoMapper = dispositivoMapper;
    }

    /**
     * Save a dispositivo.
     *
     * @param dispositivoDTO the entity to save.
     * @return the persisted entity.
     */
    public MapperDispositivoDTO save(DispositivoDTO dispositivoDTO) {
        LOG.debug("Request to save Dispositivo : {}", dispositivoDTO);
        Dispositivo dispositivo = dispositivoMapper.toEntity(dispositivoDTO);
        dispositivo = dispositivoRepository.save(dispositivo);
        return dispositivoMapper.toDto(dispositivo);
    }

    /**
     * Update a dispositivo.
     *
     * @param dispositivoDTO the entity to save.
     * @return the persisted entity.
     */
    public MapperDispositivoDTO update(DispositivoDTO dispositivoDTO) {
        LOG.debug("Request to update Dispositivo : {}", dispositivoDTO);
        Dispositivo dispositivo = dispositivoMapper.toEntity(dispositivoDTO);
        dispositivo = dispositivoRepository.save(dispositivo);
        return dispositivoMapper.toDto(dispositivo);
    }

    /**
     * Partially update a dispositivo.
     *
     * @param dispositivoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MapperDispositivoDTO> partialUpdate(DispositivoDTO dispositivoDTO) {
        LOG.debug("Request to partially update Dispositivo : {}", dispositivoDTO);

        return dispositivoRepository
            .findById(dispositivoDTO.getId())
            .map(existingDispositivo -> {
                dispositivoMapper.partialUpdate(existingDispositivo, dispositivoDTO);

                return existingDispositivo;
            })
            .map(dispositivoRepository::save)
            .map(dispositivoMapper::toDto);
    }

    /**
     * Get all the dispositivos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<MapperDispositivoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Dispositivos");
        return dispositivoRepository.findAll(pageable).map(dispositivoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<MapperDispositivoDTO> findAllNoPag() {
        LOG.debug("Request to get all Dispositivos without pagination");
        return dispositivoRepository.findAll().stream().map(dispositivoMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Get all the dispositivos with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MapperDispositivoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return dispositivoRepository.findAllWithEagerRelationships(pageable).map(dispositivoMapper::toDto);
    }

    /**
     * Get one dispositivo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MapperDispositivoDTO> findOne(Long id) {
        LOG.debug("Request to get Dispositivo : {}", id);
        return dispositivoRepository.findOneWithEagerRelationships(id).map(dispositivoMapper::toDto);
    }

    /**
     * Delete the dispositivo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Dispositivo : {}", id);
        dispositivoRepository.deleteById(id);
    }
}
