package movilidaddigital.datos.dao;

import java.util.List;
import movilidaddigital.datos.dto.VehiculoDTO;

public interface VehiculoDAO {
    List<VehiculoDTO> obtenerTodos() throws Exception;
}
