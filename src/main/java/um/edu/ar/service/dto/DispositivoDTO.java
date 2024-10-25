package um.edu.ar.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link um.edu.ar.domain.Dispositivo} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DispositivoDTO implements Serializable {

    private Long id;

    @NotNull
    private String codigo;

    @NotNull
    private String nombre;

    @Lob
    private String descripcion;

    @NotNull
    private BigDecimal precioBase;

    @NotNull
    private String moneda;

    private Set<CaracteristicaDTO> caracteristicas;

    private Set<PersonalizacionDTO> personalizaciones;

    private Set<AdicionalDTO> adicionales = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public Set<CaracteristicaDTO> getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(Set<CaracteristicaDTO> caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public Set<PersonalizacionDTO> getPersonalizaciones() { // Ahora es MapperPersonalizacionDTO
        return personalizaciones;
    }

    public void setPersonalizaciones(Set<PersonalizacionDTO> personalizaciones) {
        this.personalizaciones = personalizaciones;
    }

    public Set<AdicionalDTO> getAdicionales() {
        return adicionales;
    }

    public void setAdicionales(Set<AdicionalDTO> adicionales) {
        this.adicionales = adicionales;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DispositivoDTO)) {
            return false;
        }

        DispositivoDTO dispositivoDTO = (DispositivoDTO) o;
        if (this.id == null) {
            return false;
        }
        return (
            Objects.equals(this.id, dispositivoDTO.id) &&
            Objects.equals(this.codigo, dispositivoDTO.codigo) &&
            Objects.equals(this.nombre, dispositivoDTO.nombre) &&
            Objects.equals(this.descripcion, dispositivoDTO.descripcion) &&
            Objects.equals(this.precioBase, dispositivoDTO.precioBase) &&
            Objects.equals(this.moneda, dispositivoDTO.moneda) &&
            Objects.equals(this.adicionales, dispositivoDTO.adicionales)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DispositivoDTO{" +
            "id=" + getId() +
            ", codigo='" + getCodigo() + "'" +
            ", nombre='" + getNombre() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", precioBase=" + getPrecioBase() +
            ", moneda='" + getMoneda() + "'" +
            ", adicionales=" + getAdicionales() +
            "}";
    }
}
