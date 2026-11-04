package movilidaddigital.datos.dao.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import movilidaddigital.datos.dao.VehiculoDAO;
import movilidaddigital.datos.dto.VehiculoDTO;

public class VehiculoDaoBd implements VehiculoDAO {
    private final ConexionBD conexion;
    private final String tabla;

    public VehiculoDaoBd(ConexionBD conexion, String schema) {
        this.conexion = conexion;
        this.tabla = schema + ".vehiculo";
    }

    @Override
    public List<VehiculoDTO> obtenerTodos() throws Exception {
        List<VehiculoDTO> resultado = new ArrayList<>();
        String sql = "SELECT patente, modelo, capacidad_pasajeros, tipo_vehiculo, "
                + "categoria_vehiculo, tipo_servicios FROM " + tabla;
        try (Connection c = conexion.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(new VehiculoDTO(rs.getString("patente"), rs.getString("modelo"),
                        rs.getInt("capacidad_pasajeros"), rs.getInt("tipo_vehiculo"),
                        rs.getInt("categoria_vehiculo"), rs.getString("tipo_servicios")));
            }
        }
        return resultado;
    }
}
