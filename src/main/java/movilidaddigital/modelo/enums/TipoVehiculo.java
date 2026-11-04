package movilidaddigital.modelo.enums;

public enum TipoVehiculo {
    AUTO(0),
    MOTO(1);

    private final int codigo;

    TipoVehiculo(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public static TipoVehiculo desdeCodigo(int codigo) {
        for (TipoVehiculo tipo : values()) {
            if (tipo.codigo == codigo) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de tipo de vehículo inválido: " + codigo);
    }
}
