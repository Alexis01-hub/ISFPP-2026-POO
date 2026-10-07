package movilidaddigital.excepciones;

public class ConductorNoDisponibleException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public ConductorNoDisponibleException(String message) {
        super(message);
    }
}
