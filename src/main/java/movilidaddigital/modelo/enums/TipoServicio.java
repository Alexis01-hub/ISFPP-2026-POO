package movilidaddigital.modelo.enums;

public enum TipoServicio {
    PASAJEROS(0),
    ENVIOS(1);

    private final int codigo;

    TipoServicio(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public static TipoServicio desdeCodigo(int codigo) {
        for (TipoServicio tipo : values()) {
            if (tipo.codigo == codigo) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de tipo de servicio inválido: " + codigo);
    }
}
