package movilidaddigital.datos.dao.txt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import movilidaddigital.datos.dao.VehiculoDAO;
import movilidaddigital.datos.dto.VehiculoDTO;

public class VehiculoDaoTxt implements VehiculoDAO {
    private final String archivo;
    public VehiculoDaoTxt(String archivo) { this.archivo = archivo; }
    public List<VehiculoDTO> obtenerTodos() throws IOException {
        List<VehiculoDTO> resultado = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(abrir(), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] c = campos(linea);
                if (c.length >= 6 && c.length <= 7) {
                    resultado.add(new VehiculoDTO(c[0], c[1], Integer.parseInt(c[2]), c[3], c[4],
                            c[5] + (c.length == 7 ? ";" + c[6] : "")));
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
