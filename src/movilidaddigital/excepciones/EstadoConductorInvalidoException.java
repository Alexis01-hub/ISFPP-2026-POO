package movilidaddigital.excepciones;

public class EstadoConductorInvalidoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EstadoConductorInvalidoException(String message) {
        super(message);
    }
}
