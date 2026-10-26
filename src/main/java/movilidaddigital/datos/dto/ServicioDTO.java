package movilidaddigital.datos.dto;

public class ServicioDTO {
    private final String nombre;
    private final double tarifaBase;
    private final double precioKm;
    private final double precioMinuto;
    private final String tipoVehiculo;
    private final String categoriaVehiculo;
    private final String tipoServicio;

    public ServicioDTO(String nombre, double tarifaBase, double precioKm, double precioMinuto,
                       String tipoVehiculo, String categoriaVehiculo, String tipoServicio) {
        this.nombre = nombre;
        this.tarifaBase = tarifaBase;
        this.precioKm = precioKm;
        this.precioMinuto = precioMinuto;
        this.tipoVehiculo = tipoVehiculo;
        this.categoriaVehiculo = categoriaVehiculo;
        this.tipoServicio = tipoServicio;
    }

    public String getNombre() { return nombre; }
    public double getTarifaBase() { return tarifaBase; }
    public double getPrecioKm() { return precioKm; }
    public double getPrecioMinuto() { return precioMinuto; }
    public String getTipoVehiculo() { return tipoVehiculo; }
    public String getCategoriaVehiculo() { return categoriaVehiculo; }
    public String getTipoServicio() { return tipoServicio; }
}
