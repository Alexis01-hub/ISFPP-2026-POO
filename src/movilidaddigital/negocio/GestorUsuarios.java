package movilidaddigital.negocio;

import movilidaddigital.excepciones.EstadoConductorInvalidoException;
import movilidaddigital.excepciones.UsuarioNoEsConductorException;
import movilidaddigital.excepciones.UsuarioYaRegistradoException;
import movilidaddigital.modelo.Conductor;
import movilidaddigital.modelo.Usuario;
import movilidaddigital.modelo.Vehiculo;
import movilidaddigital.modelo.enums.CategoriaVehiculo;
import movilidaddigital.modelo.enums.EstadoConductor;

import java.util.ArrayList;
import net.datastructures.ChainHashMap;
import java.util.List;
import net.datastructures.Map;

public class GestorUsuarios {
    private final List<Usuario> usuarios = new ArrayList<>();
    private final Map<String, Usuario> porEmail = new ChainHashMap<>(); // clave: email en minusculas, valor: usuario

    public GestorUsuarios() {
    }

    /**
     * Arranca con usuarios ya cargados (por ejemplo desde el archivo). Ignora emails repetidos.
     *
     * @param iniciales lista de usuarios iniciales del .txt. Se ignoran los repetidos (por email).
     */
    public GestorUsuarios(List<Usuario> iniciales) {
        for (Usuario u : iniciales) {
            if (porEmail.get(clave(u.getEmail())) == null) {
                agregar(u);
            }
        }
    }

    /**
     * Convierte el email a minusculas y le quita espacios al principio y al final.
     *
     * @param email mail a normalizar
     * @return email normalizado (en minusculas y sin espacios al principio ni al final)
     */
    private static String clave(String email) {
        return email.trim().toLowerCase();
    }

    /**
     * Agrega un usuario a la lista y al mapa de busqueda por email.
     *
     * @param u usuario a agregar
     */
    private void agregar(Usuario u) {
        usuarios.add(usuarios.size(), u); // agrega al final de la lista
        porEmail.put(clave(u.getEmail()), u); // agrega al mapa la clave mail ya normalizada, y el usuario
    }

    /**
     * Registra un nuevo usuario, si el email no esta repetido. El nombre y el email son obligatorios.
     * @param nombre nombre del usuario
     * @param telefono telefono del usuario
     * @param email email del usuario
     * @return el usuario registrado
     */
    public synchronized Usuario registrar(String nombre, String telefono, String email) {
        if (nombre == null || nombre.isBlank() || email == null || email.isBlank()) {
            throw new IllegalArgumentException("Nombre y email son obligatorios");
        }
        if (porEmail.get(clave(email)) != null) {
            throw new UsuarioYaRegistradoException("Ya existe un usuario con el email " + email);
        }
        Usuario u = new Usuario(nombre, telefono, email);
        agregar(u); // agrega a la lista y al mapa
        return u;
    }

    /**
     * Busca un usuario por email. Devuelve null si no existe.
     * @param email email del usuario a buscar
     * @return el usuario encontrado, o null si no existe
     */
    public synchronized Usuario buscarPorEmail(String email) {
        return porEmail.get(clave(email));
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    /**
     * Registra a un usuario como conductor, con su licencia y su primer vehiculo. La patente del vehiculo debe ser unica.
     * @param usuario usuario a registrar como conductor
     * @param licencia licencia del conductor
     * @param vehiculo primer vehiculo del conductor
     */
    public synchronized void altaConductor(Usuario usuario, String licencia, Vehiculo vehiculo) {
        if (licencia == null || licencia.isBlank()) {
            throw new IllegalArgumentException("La licencia es obligatoria");
        }
        validarPatenteLibre(vehiculo); // valida que la patente del vehiculo no este en uso por otro conductor
        usuario.altaConductor(licencia, vehiculo); // crea el objeto Conductor y lo asocia al usuario
    }

    /**
     * Agrega un vehiculo al conductor del usuario. La patente del vehiculo debe ser unica.
     * @param usuario usuario que es conductor
     * @param vehiculo vehiculo a agregar al conductor
     */
    public synchronized void agregarVehiculo(Usuario usuario, Vehiculo vehiculo) {
        conductorDe(usuario); // lanza excepcion si el usuario no es conductor
        validarPatenteLibre(vehiculo); // valida que la patente del vehiculo no este en uso por otro conductor
        usuario.getConductor().agregarVehiculo(vehiculo); // agrega el vehiculo al usuario conductor.
    }

    /**
     * Permite al conductor seleccionar un vehiculo de su lista de vehiculos, y una categoria minima para aceptar viajes.
     * Solo se puede cambiar de vehiculo estando fuera de servicio.
     * @param usuario usuario que es conductor
     * @param vehiculo vehiculo a seleccionar (debe estar en la lista de vehiculos del conductor)
     * @param categoriaMinima categoria minima para aceptar viajes (no puede ser mayor a la del vehiculo seleccionado)
     */
    public synchronized void seleccionarVehiculo(Usuario usuario, Vehiculo vehiculo, CategoriaVehiculo categoriaMinima) {
        Conductor c = conductorDe(usuario);
        if (c.getEstadoConductor() != EstadoConductor.FUERA_DE_SERVICIO) {
            throw new EstadoConductorInvalidoException("Solo se puede cambiar de vehiculo estando fuera de servicio");
        }
        if (categoriaMinima.getValor() > vehiculo.getCategoriaVehiculo().getValor()) {
            throw new IllegalArgumentException("La categoria minima no puede superar la del vehiculo");
        }
        c.setVehiculoActivo(vehiculo); // selecciona el vehiculo activo del conductor
        c.setCategoriaVehiculoActivo(categoriaMinima); // selecciona la categoria minima para aceptar viajes
    }

    /**
     * Permite al conductor ponerse disponible para recibir viajes. Solo se puede poner disponible si tiene un vehiculo activo seleccionado.
     * @param usuario usuario que es conductor
     */
    public synchronized void ponerDisponible(Usuario usuario) {
        cambiarEstado(usuario, EstadoConductor.DISPONIBLE);
    }

    /**
     * Permite al conductor ponerse fuera de servicio. Solo se puede poner fuera de servicio si no esta en un viaje.
     * @param usuario usuario que es conductor
     */
    public synchronized void ponerFueraDeServicio(Usuario usuario) {
        cambiarEstado(usuario, EstadoConductor.FUERA_DE_SERVICIO);
    }

    /**
     * Permite al conductor cambiar su estado. Solo se puede cambiar de estado si no esta en un viaje.
     * @param usuario usuario que es conductor
     * @param nuevo nuevo estado del conductor
     */
    private void cambiarEstado(Usuario usuario, EstadoConductor nuevo) {
        Conductor c = conductorDe(usuario);
        EstadoConductor actual = c.getEstadoConductor();
        if (actual == EstadoConductor.VIAJE_A_ORIGEN || actual == EstadoConductor.VIAJE_A_DESTINO) {
            throw new EstadoConductorInvalidoException("El conductor esta en un viaje");
        }
        c.setEstadoConductor(nuevo);
    }

    /**
     * Devuelve el perfil de conductor del usuario (su licencia, vehiculos y estado).
     * A diferencia de Usuario.getConductor(), que devuelve null si el usuario no es conductor,
     * este metodo lanza una excepcion para que quien lo use no tenga que revisar el null.
     *
     * @param usuario usuario del que se quiere obtener el perfil de conductor
     * @return el conductor del usuario (nunca null)
     * @throws UsuarioNoEsConductorException si el usuario no esta dado de alta como conductor
     */
    private Conductor conductorDe(Usuario usuario) {
        if (usuario.getConductor() == null) {
            throw new UsuarioNoEsConductorException("El usuario no es conductor");
        }
        return usuario.getConductor();
    }

    /**
     * Valida que la patente del vehiculo no este en uso por otro conductor. Si esta en uso, lanza una excepcion.
     * @param vehiculo vehiculo a validar
     */
    private void validarPatenteLibre(Vehiculo vehiculo) {
        for (Usuario u : usuarios) {
            if (u.getConductor() == null) continue; // si el usuario no es conductor, no tiene vehiculos
            for (Vehiculo v : u.getConductor().getVehiculos()) {
                if (v.getPatente().equalsIgnoreCase(vehiculo.getPatente())) {
                    throw new IllegalArgumentException("La patente " + vehiculo.getPatente() + " ya pertenece a otro conductor");
                }
            }
        }
    }
}
