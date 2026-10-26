package movilidaddigital.datos.dto;

public class VehiculoDTO {
    private final String patente;
    private final String modelo;
    private final int capacidadPasajeros;
    private final String tipoVehiculo;
    private final String categoriaVehiculo;
    private final String tiposServicio;

    public VehiculoDTO(String patente, String modelo, int capacidadPasajeros,
                       String tipoVehiculo, String categoriaVehiculo, String tiposServicio) {
        this.patente = patente;
        this.modelo = modelo;
        this.capacidadPasajeros = capacidadPasajeros;
        this.tipoVehiculo = tipoVehiculo;
        this.categoriaVehiculo = categoriaVehiculo;
        this.tiposServicio = tiposServicio;
    }

    public String getPatente() { return patente; }
    public String getModelo() { return modelo; }
    public int getCapacidadPasajeros() { return capacidadPasajeros; }
    public String getTipoVehiculo() { return tipoVehiculo; }
    public String getCategoriaVehiculo() { return categoriaVehiculo; }
    public String getTiposServicio() { return tiposServicio; }
}
