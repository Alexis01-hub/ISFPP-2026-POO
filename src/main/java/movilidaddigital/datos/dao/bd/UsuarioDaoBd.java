package movilidaddigital.datos.dao.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import movilidaddigital.datos.dao.UsuarioDAO;
import movilidaddigital.datos.dto.UsuarioDTO;

public class UsuarioDaoBd implements UsuarioDAO {
    private final ConexionBD conexion;
    private final String tabla;

    public UsuarioDaoBd(ConexionBD conexion, String schema) {
        this.conexion = conexion;
        this.tabla = schema + ".usuario";
    }

    @Override
    public List<UsuarioDTO> obtenerTodos() throws Exception {
        List<UsuarioDTO> resultado = new ArrayList<>();
        String sql = "SELECT u.nombre, u.telefono, u.email, u.licencia_conducir, "
                + "COALESCE(string_agg(cv.patente, ';' ORDER BY cv.patente), '') AS patentes "
                + "FROM " + tabla + " u "
                + "LEFT JOIN " + tabla.replace(".usuario", ".conductor_vehiculo")
                + " cv ON cv.email = u.email "
                + "GROUP BY u.nombre, u.telefono, u.email, u.licencia_conducir";
        try (Connection c = conexion.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(new UsuarioDTO(rs.getString("nombre"), rs.getString("telefono"),
                        rs.getString("email"), rs.getString("licencia_conducir"),
                        rs.getString("patentes")));
            }
        }
        return resultado;
    }
}
