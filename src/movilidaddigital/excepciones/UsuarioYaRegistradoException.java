package movilidaddigital.excepciones;

public class UsuarioYaRegistradoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UsuarioYaRegistradoException(String message) {
        super(message);
    }
}
