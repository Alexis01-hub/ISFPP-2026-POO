package movilidaddigital.datos.dao;

import java.util.List;
import movilidaddigital.datos.dto.ServicioDTO;

public interface ServicioDAO {
    List<ServicioDTO> obtenerTodos() throws Exception;
}
