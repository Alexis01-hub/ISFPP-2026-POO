package movilidaddigital.datos.dao;

import java.util.List;
import movilidaddigital.datos.dto.UsuarioDTO;

public interface UsuarioDAO {
    List<UsuarioDTO> obtenerTodos() throws Exception;
}
