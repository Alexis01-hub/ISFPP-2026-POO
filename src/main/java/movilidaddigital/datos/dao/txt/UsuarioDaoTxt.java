package movilidaddigital.datos.dao.txt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import movilidaddigital.datos.dao.UsuarioDAO;
import movilidaddigital.datos.dto.UsuarioDTO;

public class UsuarioDaoTxt implements UsuarioDAO {
    private final String archivo;
    public UsuarioDaoTxt(String archivo) { this.archivo = archivo; }
    public List<UsuarioDTO> obtenerTodos() throws IOException {
        List<UsuarioDTO> resultado = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(abrir(), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] c = campos(linea);
                if (c.length >= 3) {
                    String patentes = c.length > 4 ? String.join(";", java.util.Arrays.copyOfRange(c, 4, c.length)) : "";
                    resultado.add(new UsuarioDTO(c[0], c[1], c[2], c.length > 3 ? c[3] : "", patentes));
                }
            }
        }
        return resultado;
    }
    private InputStream abrir() {
        InputStream entrada = getClass().getClassLoader().getResourceAsStream(archivo);
        if (entrada == null) throw new IllegalStateException("No se encontro el archivo: " + archivo);
        return entrada;
    }
    private String[] campos(String linea) {
        if (linea.trim().isEmpty() || linea.trim().startsWith("#")) return new String[0];
        String[] campos = linea.trim().split(";");
        for (int i = 0; i < campos.length; i++) campos[i] = campos[i].trim();
        return campos;
    }
}
