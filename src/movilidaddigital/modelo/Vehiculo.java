package movilidaddigital.modelo;

import movilidaddigital.modelo.enums.*;

import java.util.ArrayList;
import java.util.List;

public class Vehiculo {
    private String patente;
    private String modelo;
    private int capacidadPasajeros;
    private CategoriaVehiculo categoriaVehiculo;
    private TipoVehiculo tipoVehiculo;
    private List<TipoServicio> tipoServicios;

    /**
     * crea el vehiculo con su primer tipo de servicio (tiene que tener al menos uno)
     * si presta un segundo tipo, se agrega con agregarTipoServicio()
     */
    public Vehiculo(String patente, String modelo, int capacidadPasajeros, CategoriaVehiculo categoriaVehiculo, TipoVehiculo tipoVehiculo, TipoServicio tipoServicio) {
        this.patente = patente;
        this.modelo = modelo;
        this.capacidadPasajeros = capacidadPasajeros;
        this.categoriaVehiculo = categoriaVehiculo;
        this.tipoVehiculo = tipoVehiculo;
        this.tipoServicios = new ArrayList<>();
        this.tipoServicios.add(tipoServicio); // Agrega el tipo de servicio al listado
    }

    /**
     * agrega un tipo de servicio al listado de tipos de servicios del vehiculo. si el vehiculo ya lo tiene, no lo repite.
     */
    public void agregarTipoServicio(TipoServicio tipoServicio) {
        if (!tipoServicios.contains(tipoServicio)) {
            tipoServicios.add(tipoServicio);
        }
    }

    public String getPatente() {
        return patente;
    }

    public String getModelo() {
        return modelo;
    }

    public int getCapacidadPasajeros() {
        return capacidadPasajeros;
    }

    public CategoriaVehiculo getCategoriaVehiculo() {
        return categoriaVehiculo;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public List<TipoServicio> getTipoServicios() {
        return tipoServicios;
    }
}
