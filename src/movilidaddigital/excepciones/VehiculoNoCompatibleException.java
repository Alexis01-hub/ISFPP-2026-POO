package movilidaddigital.excepciones;

public class VehiculoNoCompatibleException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public VehiculoNoCompatibleException(String message) {
        super(message);
    }
}
