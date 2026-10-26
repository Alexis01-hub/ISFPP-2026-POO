package movilidaddigital.modelo;

import movilidaddigital.excepciones.UsuarioNoEsConductorException;
import movilidaddigital.modelo.enums.RolUsuario;
import movilidaddigital.modelo.enums.EstadoConductor;

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
     * Cambia el rol activo del usuario. Si se cambia a CONDUCTOR, verifica que el usuario tenga un objeto Conductor asociado y que esté fuera de servicio.
     * Si se cambia a CLIENTE, pone al conductor en estado FUERA_DE_SERVICIO.
     * @param rolNuevo el nuevo rol activo que se desea asignar al usuario
     */
    public void cambiarRolActivo(RolUsuario rolNuevo) {
        if (rolNuevo == RolUsuario.CONDUCTOR) {
            if (conductor == null) {
                throw new UsuarioNoEsConductorException(
                        "El usuario no esta dado de alta como conductor");
            }

            if (cliente.enViaje()) {
                throw new IllegalStateException(
                        "No se puede cambiar a conductor durante un viaje activo");
            }

            if (conductor.getEstadoConductor() != EstadoConductor.FUERA_DE_SERVICIO) {
                throw new IllegalStateException(
                        "El conductor debe estar fuera de servicio para cambiar de rol");
            }
        }

        if (rolNuevo == RolUsuario.CLIENTE && conductor != null) {
            conductor.setEstado(EstadoConductor.FUERA_DE_SERVICIO);
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
