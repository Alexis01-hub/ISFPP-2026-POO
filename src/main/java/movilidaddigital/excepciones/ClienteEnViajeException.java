package movilidaddigital.excepciones;

/** Indica que un cliente intentó solicitar otro viaje activo. */
public class ClienteEnViajeException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    public ClienteEnViajeException(String message) {
        super(message);
    }
}
