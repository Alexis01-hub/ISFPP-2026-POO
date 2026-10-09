package movilidaddigital.modelo;

import movilidaddigital.modelo.enums.*;

public class Servicio {
    private String nombre;
    private double tarifaBase;
    private double precioKm;
    private double precioMinuto;
    private CategoriaVehiculo categoriaVehiculo;
    private TipoVehiculo tipoVehiculo;
    private TipoServicio tipoServicio;

    public Servicio(String nombre, double tarifaBase, double precioKm, double precioMinuto,
                    TipoVehiculo tipoVehiculo,
                    CategoriaVehiculo categoriaVehiculo,
                    TipoServicio tipoServicio) {
        this.nombre = nombre;
        this.tarifaBase = tarifaBase;
        this.precioKm = precioKm;
        this.precioMinuto = precioMinuto;
        this.categoriaVehiculo = categoriaVehiculo;
        this.tipoVehiculo = tipoVehiculo;
        this.tipoServicio = tipoServicio;
    }

    public String getNombre() {
        return nombre;
    }

    public double getTarifaBase() {
        return tarifaBase;
    }

    public double getPrecioKm() {
        return precioKm;
    }

    public double getPrecioMinuto() {
        return precioMinuto;
    }

    public CategoriaVehiculo getCategoriaVehiculo() {
        return categoriaVehiculo;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public double calcularCosto(double km, double minutos){
        return tarifaBase + (precioKm * km) + (precioMinuto * minutos);
    }

    public void setCategoriaVehiculo(CategoriaVehiculo categoriaVehiculo) {
        this.categoriaVehiculo = categoriaVehiculo;
    }
}
