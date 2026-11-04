package movilidaddigital.datos.dao.bd;

import movilidaddigital.datos.dao.DAOFactory;
import movilidaddigital.datos.dao.ServicioDAO;
import movilidaddigital.datos.dao.UsuarioDAO;
import movilidaddigital.datos.dao.VehiculoDAO;

public class BdDAOFactory implements DAOFactory {
    private final ConexionBD conexion;
    private final String schema;

    public BdDAOFactory(String url, String usuario, String password, String schema) {
        this.conexion = new ConexionBD(url, usuario, password);
        this.schema = validarIdentificador(schema);
    }

    @Override
    public ServicioDAO getServicioDAO() {
        return new ServicioDaoBd(conexion, schema);
    }

    @Override
    public VehiculoDAO getVehiculoDAO() {
        return new VehiculoDaoBd(conexion, schema);
    }

    @Override
    public UsuarioDAO getUsuarioDAO() {
        return new UsuarioDaoBd(conexion, schema);
    }

    private static String validarIdentificador(String valor) {
        if (valor == null || !valor.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException("Schema inválido");
        }
        return valor;
    }
}
