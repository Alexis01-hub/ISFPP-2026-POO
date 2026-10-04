package movilidaddigital.modelo.enums;

public enum EstadoViaje {
    SOLICITADO,
    ACEPTADO,
    INICIADO,
    FINALIZADO,
    CANCELADO,
    RECHAZADO;

    /**
     * indica si el viaje termino
     *
     * @return true si el viaje termino, false en caso contrario
     */
    public boolean esFinal(){
        return this == FINALIZADO || this == CANCELADO || this == RECHAZADO;
    }
}
