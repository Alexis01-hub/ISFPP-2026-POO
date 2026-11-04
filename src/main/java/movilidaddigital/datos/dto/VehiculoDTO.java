package movilidaddigital.datos.dto;

public class VehiculoDTO {
    private final String patente;
    private final String modelo;
    private final int capacidadPasajeros;
    private final String tipoVehiculo;
    private final String categoriaVehiculo;
    private final String tiposServicio;
    private final Integer codigoTipoVehiculo;
    private final Integer codigoCategoriaVehiculo;

    public VehiculoDTO(String patente, String modelo, int capacidadPasajeros,
                       String tipoVehiculo, String categoriaVehiculo, String tiposServicio) {
        this.patente = patente;
        this.modelo = modelo;
        this.capacidadPasajeros = capacidadPasajeros;
        this.tipoVehiculo = tipoVehiculo;
        this.categoriaVehiculo = categoriaVehiculo;
        this.tiposServicio = tiposServicio;
        this.codigoTipoVehiculo = null;
        this.codigoCategoriaVehiculo = null;
    }

    public VehiculoDTO(String patente, String modelo, int capacidadPasajeros,
                       int codigoTipoVehiculo, String categoriaVehiculo, String tiposServicio) {
        this.patente = patente;
        this.modelo = modelo;
        this.capacidadPasajeros = capacidadPasajeros;
        this.tipoVehiculo = null;
        this.categoriaVehiculo = categoriaVehiculo;
        this.tiposServicio = tiposServicio;
        this.codigoTipoVehiculo = codigoTipoVehiculo;
        this.codigoCategoriaVehiculo = null;
    }

    public VehiculoDTO(String patente, String modelo, int capacidadPasajeros,
                       int codigoTipoVehiculo, int codigoCategoriaVehiculo, String tiposServicio) {
        this.patente = patente;
        this.modelo = modelo;
        this.capacidadPasajeros = capacidadPasajeros;
        this.tipoVehiculo = null;
        this.categoriaVehiculo = null;
        this.tiposServicio = tiposServicio;
        this.codigoTipoVehiculo = codigoTipoVehiculo;
        this.codigoCategoriaVehiculo = codigoCategoriaVehiculo;
    }

    public String getPatente() { return patente; }
    public String getModelo() { return modelo; }
    public int getCapacidadPasajeros() { return capacidadPasajeros; }
    public String getTipoVehiculo() { return tipoVehiculo; }
    public int getCodigoTipoVehiculo() { return codigoTipoVehiculo; }
    public int getCodigoCategoriaVehiculo() { return codigoCategoriaVehiculo; }
    public String getCategoriaVehiculo() { return categoriaVehiculo; }
    public String getTiposServicio() { return tiposServicio; }
}
