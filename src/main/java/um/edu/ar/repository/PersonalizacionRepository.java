package um.edu.ar.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import um.edu.ar.domain.Personalizacion;

/**
 * Spring Data JPA repository for the Personalizacion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PersonalizacionRepository extends JpaRepository<Personalizacion, Long> {}
