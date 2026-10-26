package movilidaddigital.modelo.enums;

public enum CategoriaVehiculo {
    ESTANDAR(1),
    CONFORT(2),
    PREMIUM(3);

    private final int valor;

    CategoriaVehiculo(int valor) {
        this.valor = valor;
    }

    /**
     *
     * @return valor numerico de la categoria del vehiculo (1 a 3)
     */
    public int getValor() {
        return valor;
    }

}
