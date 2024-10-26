package um.edu.ar.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the {@link um.edu.ar.domain.Venta} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VentaDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime fechaVenta;

    private BigDecimal ganancia;

    private UserDTO user;

    // Inner classes para el mapeo de JSON
    private Integer idDispositivo;
    private List<Personalizacion> personalizaciones;
    private List<Adicional> adicionales;
    private BigDecimal precioFinal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(ZonedDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getGanancia() {
        return ganancia;
    }

    public void setGanancia(BigDecimal ganancia) {
        this.ganancia = ganancia;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Integer getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(Integer idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public List<Personalizacion> getPersonalizaciones() {
        return personalizaciones;
    }

    public void setPersonalizaciones(List<Personalizacion> personalizaciones) {
        this.personalizaciones = personalizaciones;
    }

    public List<Adicional> getAdicionales() {
        return adicionales;
    }

    public void setAdicionales(List<Adicional> adicionales) {
        this.adicionales = adicionales;
    }

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(BigDecimal precioFinal) {
        this.precioFinal = precioFinal;
    }

    // Inner class para "Personalizacion"
    public static class Personalizacion implements Serializable {

        private Integer id;
        private BigDecimal precio;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public BigDecimal getPrecio() {
            return precio;
        }

        public void setPrecio(BigDecimal precio) {
            this.precio = precio;
        }
    }

    // Inner class para "Adicional"
    public static class Adicional implements Serializable {

        private Integer id;
        private BigDecimal precio;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public BigDecimal getPrecio() {
            return precio;
        }

        public void setPrecio(BigDecimal precio) {
            this.precio = precio;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VentaDTO)) {
            return false;
        }

        VentaDTO ventaDTO = (VentaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ventaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return (
            "VentaDTO{" +
            "id=" +
            getId() +
            ", fechaVenta='" +
            getFechaVenta() +
            "'" +
            ", ganancia=" +
            getGanancia() +
            ", user=" +
            getUser() +
            ", idDispositivo=" +
            getIdDispositivo() +
            ", personalizaciones=" +
            getPersonalizaciones() +
            ", adicionales=" +
            getAdicionales() +
            ", precioFinal=" +
            getPrecioFinal() +
            "}"
        );
    }
}
