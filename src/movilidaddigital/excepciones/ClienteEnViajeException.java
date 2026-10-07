package movilidaddigital.excepciones;

public class ClienteEnViajeException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    public ClienteEnViajeException(String message) {
        super(message);
    }
}
