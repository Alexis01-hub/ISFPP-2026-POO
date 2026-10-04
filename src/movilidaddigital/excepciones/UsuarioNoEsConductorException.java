package movilidaddigital.excepciones;

public class UsuarioNoEsConductorException extends RuntimeException {

    private static final long serialVersionUID = 1L; // Agrega un identificador de versión para la serialización

    public UsuarioNoEsConductorException(String message) {
        super(message);
    }
}
