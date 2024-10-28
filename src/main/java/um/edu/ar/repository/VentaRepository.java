package um.edu.ar.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import um.edu.ar.domain.Venta;

/**
 * Spring Data JPA repository for the Venta entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    @Query("select venta from Venta venta where venta.user.login = ?#{authentication.name}")
    List<Venta> findByUserIsCurrentUser();

    @Query("select venta from Venta venta where venta.user.id = :userId")
    List<Venta> findByUserId(@Param("userId") Long userId);
}
