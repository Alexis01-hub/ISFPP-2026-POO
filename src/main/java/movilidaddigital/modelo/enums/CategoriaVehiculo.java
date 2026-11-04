package movilidaddigital.modelo.enums;

public enum CategoriaVehiculo {
    ESTANDAR(0), // Si en BD 0 es estándar, cámbialo a 0. Si 0 es otra categoría, defínela aquí.
    CONFORT(1),
    PREMIUM(2);

    private final int valor;

    CategoriaVehiculo(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public static CategoriaVehiculo desdeCodigo(int valor) {
        for (CategoriaVehiculo cat : values()) {
            if (cat.valor == valor) {
                return cat;
            }
        }
        throw new IllegalArgumentException("No existe CategoriaVehiculo con valor: " + valor);
    }
}
