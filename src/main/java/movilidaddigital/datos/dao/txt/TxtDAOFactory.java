package movilidaddigital.datos.dao.txt;

import movilidaddigital.datos.dao.DAOFactory;
import movilidaddigital.datos.dao.ServicioDAO;
import movilidaddigital.datos.dao.UsuarioDAO;
import movilidaddigital.datos.dao.VehiculoDAO;

public class TxtDAOFactory implements DAOFactory {
    private final String archivoServicios;
    private final String archivoVehiculos;
    private final String archivoUsuarios;

    public TxtDAOFactory(String archivoServicios, String archivoVehiculos, String archivoUsuarios) {
        this.archivoServicios = archivoServicios;
        this.archivoVehiculos = archivoVehiculos;
        this.archivoUsuarios = archivoUsuarios;
    }

    public ServicioDAO getServicioDAO() { return new ServicioDaoTxt(archivoServicios); }
    public VehiculoDAO getVehiculoDAO() { return new VehiculoDaoTxt(archivoVehiculos); }
    public UsuarioDAO getUsuarioDAO() { return new UsuarioDaoTxt(archivoUsuarios); }
}
