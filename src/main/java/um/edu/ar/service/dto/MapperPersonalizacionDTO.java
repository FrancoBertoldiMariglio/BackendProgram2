package um.edu.ar.service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("common-java:DuplicatedBlocks")
public class MapperPersonalizacionDTO implements Serializable {

    private Long id;

    @NotNull
    private String nombre;

    @NotNull
    private String descripcion;

    private DispositivoDTO dispositivo;

    private Set<OpcionDTO> opciones;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public DispositivoDTO getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(DispositivoDTO dispositivo) {
        this.dispositivo = dispositivo;
    }

    public Set<OpcionDTO> getOpciones() { // Añadido desde PersonalizacionDTO
        return opciones;
    }

    public void setOpciones(Set<OpcionDTO> opciones) { // Añadido desde PersonalizacionDTO
        this.opciones = opciones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MapperPersonalizacionDTO)) { // Corregido a MapperPersonalizacionDTO
            return false;
        }

        MapperPersonalizacionDTO that = (MapperPersonalizacionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MapperPersonalizacionDTO{" +
            "id=" + (id != null ? id : "null") +
            ", nombre='" + (nombre != null ? nombre : "null") + "'" +
            ", descripcion='" + (descripcion != null ? descripcion : "null") + "'" +
            ", dispositivo=" + (dispositivo != null ? dispositivo : "null") + // Asegurando que no sea null
            ", opciones=" + (opciones != null ? opciones : "[]") + // Añadido manejo de null para opciones
            "}";
    }
}
