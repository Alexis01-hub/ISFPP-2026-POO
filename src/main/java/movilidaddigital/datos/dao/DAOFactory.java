package movilidaddigital.datos.dao;

public interface DAOFactory {
    ServicioDAO getServicioDAO();
    VehiculoDAO getVehiculoDAO();
    UsuarioDAO getUsuarioDAO();
}
