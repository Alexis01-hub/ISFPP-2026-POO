package movilidaddigital.datos.dao.txt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import movilidaddigital.datos.dao.ServicioDAO;
import movilidaddigital.datos.dto.ServicioDTO;

public class ServicioDaoTxt implements ServicioDAO {
    private final String archivo;
    public ServicioDaoTxt(String archivo) { this.archivo = archivo; }

    public List<ServicioDTO> obtenerTodos() throws IOException {
        List<ServicioDTO> resultado = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(
                abrir(), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] c = campos(linea);
                if (c.length == 7 && !c[0].isEmpty()) {
                    resultado.add(new ServicioDTO(c[0], Double.parseDouble(c[1]),
                            Double.parseDouble(c[2]), Double.parseDouble(c[3]), c[4], c[5], c[6]));
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
