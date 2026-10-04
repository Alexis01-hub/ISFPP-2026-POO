package movilidaddigital.modelo;

import movilidaddigital.excepciones.UsuarioNoEsConductorException;
import movilidaddigital.modelo.enums.RolUsuario;

public class Usuario {
    private String nombre;
    private String telefono;
    private String email;
    private RolUsuario rolActivo;
    private Cliente cliente;
    private Conductor conductor;

    /**
     *  registra un usuario, con el rol de cliente y sin conductor asociado.
     * @param nombre
     * @param telefono
     * @param email
     */
    public Usuario(String nombre, String telefono, String email) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.rolActivo = RolUsuario.CLIENTE; // Por defecto, el rol es CLIENTE
        this.cliente = new Cliente(); // Inicializamos el cliente por defecto
        this.conductor = null; // Inicializamos el conductor como null
    }

    /**
     * Da de alta al usuario como conductor, asociando una licencia y un vehículo.
     */
    public void altaConductor(String licencia, Vehiculo vehiculo){
        if (conductor != null){
            throw new IllegalStateException("El usuario ya es conductor");
        }
        this.conductor = new Conductor(licencia, vehiculo);
    }

    /**
     * cambia de rol con que usa la aplicacion.
     * para pasar a rol de conductor, el usuario debe estar dado de alta como conductor.
     * @param rolNuevo rol que el usuario desea asumir. Puede ser CLIENTE o CONDUCTOR.
     */
    public void cambiarRolActivo(RolUsuario rolNuevo){
        if(rolNuevo == RolUsuario.CONDUCTOR && conductor == null){
            throw new UsuarioNoEsConductorException("El usuario no esta dado de alta como conductor");
        }
        this.rolActivo = rolNuevo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public RolUsuario getRolActivo() {
        return rolActivo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Devuelve el objeto Conductor asociado al usuario, si existe. Si el usuario no es un conductor, devuelve null.
     */
    public Conductor getConductor() {
        return conductor;
    }
}
