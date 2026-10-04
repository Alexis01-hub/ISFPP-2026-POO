package movilidaddigital.modelo.enums;

public enum CalificacionViaje {
    EXCELENTE(5),
    BUENO(3),
    REGULAR(2),
    MALO(1),
    MUY_BUENO(4),
    NO_CALIFICADO(0);

    private final int valor;

    CalificacionViaje(int valor) {
        this.valor = valor;
    }

    /**
     *
     * @return valor numerico de la calificacion del viaje (0 a 5)
     */
    public int getValor() {
        return valor;
    }
}
