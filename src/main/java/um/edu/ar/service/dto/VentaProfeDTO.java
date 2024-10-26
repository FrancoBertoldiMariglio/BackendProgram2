package um.edu.ar.service.dto;

import java.math.BigDecimal;
import java.util.List;

public class VentaProfeDTO {

    private static Long idVenta;
    private Long idDispositivo;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precioBase;
    private String moneda;
    private List<CaracteristicaDTO> catacteristicas;
    private List<PersonalizacionDTO> personalizaciones;
    private List<AdicionalDTO> adicionales;

    public Long getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }

    public Long getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(Long idDispositivo) {
        this.idDispositivo = idDispositivo;
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

    public List<CaracteristicaDTO> getCatacteristicas() {
        return catacteristicas;
    }

    public void setCatacteristicas(List<CaracteristicaDTO> catacteristicas) {
        this.catacteristicas = catacteristicas;
    }

    public List<PersonalizacionDTO> getPersonalizaciones() {
        return personalizaciones;
    }

    public void setPersonalizaciones(List<PersonalizacionDTO> personalizaciones) {
        this.personalizaciones = personalizaciones;
    }

    public List<AdicionalDTO> getAdicionales() {
        return adicionales;
    }

    public void setAdicionales(List<AdicionalDTO> adicionales) {
        this.adicionales = adicionales;
    }
}
