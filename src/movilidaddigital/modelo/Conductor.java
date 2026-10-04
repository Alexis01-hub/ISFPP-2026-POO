package movilidaddigital.modelo;

import movilidaddigital.modelo.enums.CategoriaVehiculo;
import movilidaddigital.modelo.enums.EstadoConductor;

import java.util.ArrayList;
import java.util.List;

public class Conductor {
    private String licenciaConducir;
    private List<Viaje> viajes;
    private CategoriaVehiculo categoriaVehiculoActivo;
    private EstadoConductor estadoConductor;
    private List<Vehiculo> vehiculos;
    private Vehiculo vehiculoActivo;

    /**
     * Crea conductor con su primir vehiculo. arranca en fuera de servicio,
     * usando ese vehiculo y aceptando solo viajes de la categoria del vehiculo.
     * @param licenciaConducir
     * @param vehiculo
     */
    public Conductor(String licenciaConducir, Vehiculo vehiculo) {
        this.licenciaConducir = licenciaConducir;
        this.vehiculos = new ArrayList<>();
        this.vehiculos.add(vehiculo);
        this.vehiculoActivo = vehiculo;
        this.categoriaVehiculoActivo = vehiculo.getCategoriaVehiculo();
        this.estadoConductor = EstadoConductor.FUERA_DE_SERVICIO;
        this.viajes = new ArrayList<>();
    }

    public String getLicenciaConducir() {
        return licenciaConducir;
    }

    public void setLicenciaConducir(String licenciaConducir) {
        this.licenciaConducir = licenciaConducir;
    }

    public List<Viaje> getViajes() {
        return viajes;
    }

    public void agregarViaje(Viaje viaje) {
        this.viajes.add(viaje);
    }

    public CategoriaVehiculo getCategoriaVehiculoActivo() {
        return categoriaVehiculoActivo;
    }

    public void setCategoriaVehiculoActivo(CategoriaVehiculo categoriaVehiculoActivo) {
        this.categoriaVehiculoActivo = categoriaVehiculoActivo;
    }

    public EstadoConductor getEstadoConductor() {
        return estadoConductor;
    }

    public void setEstadoConductor(EstadoConductor estadoConductor) {
        this.estadoConductor = estadoConductor;
    }

    public List<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public void agregarVehiculo(Vehiculo vehiculo) {
        this.vehiculos.add(vehiculo);
    }

    public Vehiculo getVehiculoActivo() {
        return vehiculoActivo;
    }

    public void setVehiculoActivo(Vehiculo vehiculoActivo) {
        if(!vehiculos.contains(vehiculoActivo)){
            throw new IllegalArgumentException("El vehiculo no pertenece al conductor");
        }
        this.vehiculoActivo = vehiculoActivo;
    }
}
