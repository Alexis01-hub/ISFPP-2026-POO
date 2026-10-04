package movilidaddigital.excepciones;

public class TransicionViajeInvalidaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TransicionViajeInvalidaException(String message) {
        super(message);
    }
}
