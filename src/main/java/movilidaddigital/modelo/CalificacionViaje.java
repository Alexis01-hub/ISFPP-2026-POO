package movilidaddigital.modelo;

public enum CalificacionViaje {
    NO_CALIFICADO(0),
    MALO(1),
    REGULAR(2),
    BUENO(3),
    MUY_BUENO(4),
    EXCELENTE(5);

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
