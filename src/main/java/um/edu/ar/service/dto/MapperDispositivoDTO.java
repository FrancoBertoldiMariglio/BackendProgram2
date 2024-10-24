package um.edu.ar.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("common-java:DuplicatedBlocks")
public class MapperDispositivoDTO implements Serializable {

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

    private Set<MapperPersonalizacionDTO> personalizaciones; // Cambiado a MapperPersonalizacionDTO

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

    public Set<AdicionalDTO> getAdicionales() {
        return adicionales;
    }

    public void setAdicionales(Set<AdicionalDTO> adicionales) {
        this.adicionales = adicionales;
    }

    public Set<CaracteristicaDTO> getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(Set<CaracteristicaDTO> caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public Set<MapperPersonalizacionDTO> getPersonalizaciones() { // Ahora es MapperPersonalizacionDTO
        return personalizaciones;
    }

    public void setPersonalizaciones(Set<MapperPersonalizacionDTO> personalizaciones) {
        this.personalizaciones = personalizaciones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MapperDispositivoDTO)) {
            return false;
        }

        MapperDispositivoDTO mapperDispositivoDTO = (MapperDispositivoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mapperDispositivoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return (
            "MapperDispositivoDTO{" +
            "id=" +
            (id != null ? id : "null") +
            ", codigo='" +
            (codigo != null ? codigo : "null") +
            "'" +
            ", nombre='" +
            (nombre != null ? nombre : "null") +
            "'" +
            ", descripcion='" +
            (descripcion != null ? descripcion : "null") +
            "'" +
            ", precioBase=" +
            (precioBase != null ? precioBase : "null") +
            ", moneda='" +
            (moneda != null ? moneda : "null") +
            "'" +
            ", caracteristicas=" +
            (caracteristicas != null ? caracteristicas : "[]") +
            ", personalizaciones=" +
            (personalizaciones != null ? personalizaciones : "[]") +
            ", adicionales=" +
            (adicionales != null ? adicionales : "[]") +
            "}"
        );
    }
}
