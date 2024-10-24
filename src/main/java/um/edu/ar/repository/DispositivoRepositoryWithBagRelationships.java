package um.edu.ar.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import um.edu.ar.domain.Dispositivo;

public interface DispositivoRepositoryWithBagRelationships {
    Optional<Dispositivo> fetchBagRelationships(Optional<Dispositivo> dispositivo);

    List<Dispositivo> fetchBagRelationships(List<Dispositivo> dispositivos);

    Page<Dispositivo> fetchBagRelationships(Page<Dispositivo> dispositivos);
}
