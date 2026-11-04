package movilidaddigital.datos.dao.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import movilidaddigital.datos.dao.ServicioDAO;
import movilidaddigital.datos.dto.ServicioDTO;

public class ServicioDaoBd implements ServicioDAO {
    private final ConexionBD conexion;
    private final String tabla;

    public ServicioDaoBd(ConexionBD conexion, String schema) {
        this.conexion = conexion;
        this.tabla = schema + ".servicio";
    }

    @Override
    public List<ServicioDTO> obtenerTodos() throws Exception {
        List<ServicioDTO> resultado = new ArrayList<>();
        String sql = "SELECT nombre, tarifa_base, precio_km, precio_minuto, "
                + "tipo_vehiculo, categoria_vehiculo, tipo_servicio FROM " + tabla;
        try (Connection c = conexion.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(new ServicioDTO(rs.getString("nombre"),
                        rs.getDouble("tarifa_base"), rs.getDouble("precio_km"),
                        rs.getDouble("precio_minuto"), rs.getInt("tipo_vehiculo"),
                        rs.getInt("categoria_vehiculo"), rs.getInt("tipo_servicio")));
            }
        }
        return resultado;
    }
}
