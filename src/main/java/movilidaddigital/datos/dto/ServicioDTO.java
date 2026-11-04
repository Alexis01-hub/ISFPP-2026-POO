package movilidaddigital.datos.dto;

public class ServicioDTO {
    private final String nombre;
    private final double tarifaBase;
    private final double precioKm;
    private final double precioMinuto;
    private final String tipoVehiculo;
    private final String categoriaVehiculo;
    private final String tipoServicio;
    private final Integer codigoTipoVehiculo;
    private final Integer codigoCategoriaVehiculo;
    private final Integer codigoTipoServicio;

    public ServicioDTO(String nombre, double tarifaBase, double precioKm, double precioMinuto,
                       String tipoVehiculo, String categoriaVehiculo, String tipoServicio) {
        this.nombre = nombre;
        this.tarifaBase = tarifaBase;
        this.precioKm = precioKm;
        this.precioMinuto = precioMinuto;
        this.tipoVehiculo = tipoVehiculo;
        this.categoriaVehiculo = categoriaVehiculo;
        this.tipoServicio = tipoServicio;
        this.codigoTipoVehiculo = null;
        this.codigoCategoriaVehiculo = null;
        this.codigoTipoServicio = null;
    }

    public ServicioDTO(String nombre, double tarifaBase, double precioKm, double precioMinuto,
                       int codigoTipoVehiculo, String categoriaVehiculo, String tipoServicio) {
        this.nombre = nombre;
        this.tarifaBase = tarifaBase;
        this.precioKm = precioKm;
        this.precioMinuto = precioMinuto;
        this.tipoVehiculo = null;
        this.categoriaVehiculo = categoriaVehiculo;
        this.tipoServicio = tipoServicio;
        this.codigoTipoVehiculo = codigoTipoVehiculo;
        this.codigoCategoriaVehiculo = null;
        this.codigoTipoServicio = null;
    }

    public ServicioDTO(String nombre, double tarifaBase, double precioKm, double precioMinuto,
                       int codigoTipoVehiculo, int codigoCategoriaVehiculo, String tipoServicio) {
        this.nombre = nombre;
        this.tarifaBase = tarifaBase;
        this.precioKm = precioKm;
        this.precioMinuto = precioMinuto;
        this.tipoVehiculo = null;
        this.categoriaVehiculo = null;
        this.tipoServicio = tipoServicio;
        this.codigoTipoVehiculo = codigoTipoVehiculo;
        this.codigoCategoriaVehiculo = codigoCategoriaVehiculo;
        this.codigoTipoServicio = null;
    }

    public ServicioDTO(String nombre, double tarifaBase, double precioKm, double precioMinuto,
                       int codigoTipoVehiculo, int codigoCategoriaVehiculo,
                       int codigoTipoServicio) {
        this.nombre = nombre;
        this.tarifaBase = tarifaBase;
        this.precioKm = precioKm;
        this.precioMinuto = precioMinuto;
        this.tipoVehiculo = null;
        this.categoriaVehiculo = null;
        this.tipoServicio = null;
        this.codigoTipoVehiculo = codigoTipoVehiculo;
        this.codigoCategoriaVehiculo = codigoCategoriaVehiculo;
        this.codigoTipoServicio = codigoTipoServicio;
    }

    public String getNombre() { return nombre; }
    public double getTarifaBase() { return tarifaBase; }
    public double getPrecioKm() { return precioKm; }
    public double getPrecioMinuto() { return precioMinuto; }
    public String getTipoVehiculo() { return tipoVehiculo; }
    public int getCodigoTipoVehiculo() { return codigoTipoVehiculo; }
    public int getCodigoCategoriaVehiculo() { return codigoCategoriaVehiculo; }
    public int getCodigoTipoServicio() { return codigoTipoServicio; }
    public String getCategoriaVehiculo() { return categoriaVehiculo; }
    public String getTipoServicio() { return tipoServicio; }
}
