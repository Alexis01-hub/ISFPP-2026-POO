package movilidaddigital.datos;

import java.util.List;
import movilidaddigital.datos.dao.DAOFactory;
import movilidaddigital.datos.dao.DAOLoader;
import movilidaddigital.datos.dto.ServicioDTO;
import movilidaddigital.datos.dto.UsuarioDTO;
import movilidaddigital.datos.dto.VehiculoDTO;

/**
 * Fachada de lectura para la aplicacion. La capa de negocio recibe modelos;
 * esta clase es el punto donde, en el futuro, se mapearan los DTO a modelos.
 */
public final class RepositorioDatos {
    private final DAOFactory fabrica;

    public RepositorioDatos() {
        this(DAOLoader.getFactory());
    }

    public RepositorioDatos(DAOFactory fabrica) {
        if (fabrica == null) throw new IllegalArgumentException("La fabrica no puede ser null");
        this.fabrica = fabrica;
    }

    public List<ServicioDTO> obtenerServicios() throws Exception {
        return fabrica.getServicioDAO().obtenerTodos();
    }

    public List<VehiculoDTO> obtenerVehiculos() throws Exception {
        return fabrica.getVehiculoDAO().obtenerTodos();
    }

    public List<UsuarioDTO> obtenerUsuarios() throws Exception {
        return fabrica.getUsuarioDAO().obtenerTodos();
    }
}
