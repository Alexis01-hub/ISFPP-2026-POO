package movilidaddigital.datos;

import movilidaddigital.modelo.Servicio;
import movilidaddigital.modelo.Usuario;
import movilidaddigital.modelo.Vehiculo;
import movilidaddigital.modelo.enums.CategoriaVehiculo;
import movilidaddigital.modelo.enums.TipoServicio;
import movilidaddigital.modelo.enums.TipoVehiculo;
import net.datastructures.ChainHashMap;
import net.datastructures.Map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Lee los archivos de datos (servicios, vehiculos y usuarios) y arma con ellos los
 * objetos del modelo. Los nombres de los archivos salen de config.properties.
 *
 * Usa solo las estructuras de net.datastructures:
 * Map (ChainHashMap) para las búsquedas por clave.
 *
 * Reglas generales de los archivos: un registro por linea, campos separados por ';',
 * las lineas vacías y las que empiezan con '#' se ignoran, y un ';' al final de la
 * linea es opcional.
 *
 * Si una linea tiene un problema (campos de menos, un dato invalido, una patente que
 * no existe...) NO se corta la carga: la linea problematica se ignora, o se carga solo
 * lo que sirve, y el problema queda anotado en getAdvertencias().
 *
 * Orden de uso (los usuarios necesitan los vehiculos ya cargados):
 *   CargadorDatos cargador = new CargadorDatos();
 *   List;Servicio; servicios = cargador.cargarServicios();
 *   Map;String, Vehiculo; vehiculos = cargador.cargarVehiculos();
 *   List; Usuario; usuarios = cargador.cargarUsuarios(vehiculos);
 *   List;String; problemas = cargador.getAdvertencias();
 */
public class CargadorDatos {

    // Una linea de datos del archivo: su numero (para los mensajes) y sus campos ya separados.
    private static class Linea {
        private final int numero;
        private final String[] campos;

        private Linea(int numero, String[] campos) {
            this.numero = numero;
            this.campos = campos;
        }
    }

    private final List<String> advertencias;

    public CargadorDatos() {
        this.advertencias = new ArrayList<>();
    }

    /**
     * Carga los servicios. Formato de cada linea:
     * nombre;tarifaBase;precioKm;precioMinuto;TipoVehiculo;CategoriaVehiculo;TipoServicio
     */
    public List<Servicio> cargarServicios() {
        String archivo = Configuracion.archivoServicios();
        List<Servicio> servicios = new ArrayList<>();
        Map<String, Boolean> nombres = new ChainHashMap<>();

        for (Linea linea : leerLineas(archivo)) {
            String[] campos = linea.campos;
            if (campos.length != 7) {
                advertir(archivo, linea, "se esperaban 7 campos y hay " + campos.length + ", se ignora la linea");
                continue;
            }

            try {
                String nombre = campos[0];
                double tarifaBase = leerDecimal(campos[1], "tarifaBase");
                double precioKm = leerDecimal(campos[2], "precioKm");
                double precioMinuto = leerDecimal(campos[3], "precioMinuto");
                TipoVehiculo tipoVehiculo = leerEnum(TipoVehiculo.class, campos[4], "TipoVehiculo");
                CategoriaVehiculo categoria = leerEnum(CategoriaVehiculo.class, campos[5], "CategoriaVehiculo");
                TipoServicio tipoServicio = leerEnum(TipoServicio.class, campos[6], "TipoServicio");

                if (nombre.isEmpty()) {
                    throw new IllegalArgumentException("el nombre es obligatorio");
                }
                if (tarifaBase < 0 || precioKm < 0 || precioMinuto < 0) {
                    throw new IllegalArgumentException("las tarifas no pueden ser negativas");
                }
                if (nombres.put(nombre.toLowerCase(), Boolean.TRUE) != null) {
                    advertir(archivo, linea, "el servicio '" + nombre + "' esta repetido, se ignora la linea");
                    continue;
                }

                // OJO: en el archivo el tipo de vehiculo va antes que la categoria,
                // pero el constructor de Servicio los recibe al reves.
                servicios.add(servicios.size(), new Servicio(nombre, tarifaBase, precioKm, precioMinuto,
                        categoria, tipoVehiculo, tipoServicio));
            } catch (IllegalArgumentException e) {
                advertir(archivo, linea, e.getMessage() + ", se ignora la linea");
            }
        }
        return servicios;
    }

    /**
     * Carga los vehiculos y los devuelve en un mapa patente -> vehiculo. Formato de cada linea:
     * patente;modelo;capacidadPasajeros;TipoVehiculo;CategoriaVehiculo;TipoServicio[;TipoServicio]
     *
     * El mapa sirve para buscar un vehiculo por su patente; no conserva el orden del archivo.
     */
    public Map<String, Vehiculo> cargarVehiculos() {
        String archivo = Configuracion.archivoVehiculos();
        Map<String, Vehiculo> vehiculos = new ChainHashMap<>();

        for (Linea linea : leerLineas(archivo)) {
            String[] campos = linea.campos;
            if (campos.length < 6 || campos.length > 7) {
                advertir(archivo, linea, "se esperaban 6 o 7 campos y hay " + campos.length + ", se ignora la linea");
                continue;
            }

            try {
                String patente = campos[0];
                String modelo = campos[1];
                int capacidad = leerEntero(campos[2], "capacidadPasajeros");
                TipoVehiculo tipoVehiculo = leerEnum(TipoVehiculo.class, campos[3], "TipoVehiculo");
                CategoriaVehiculo categoria = leerEnum(CategoriaVehiculo.class, campos[4], "CategoriaVehiculo");
                TipoServicio primerServicio = leerEnum(TipoServicio.class, campos[5], "TipoServicio");

                if (patente.isEmpty() || modelo.isEmpty()) {
                    throw new IllegalArgumentException("la patente y el modelo son obligatorios");
                }
                if (capacidad < 1) {
                    throw new IllegalArgumentException("la capacidad debe ser al menos 1");
                }
                // get devuelve null si la patente no esta en el mapa
                if (vehiculos.get(patente) != null) {
                    advertir(archivo, linea, "la patente " + patente + " esta repetida, se ignora la linea");
                    continue;
                }

                // OJO: en el archivo el tipo de vehiculo va antes que la categoria,
                // pero el constructor de Vehiculo los recibe al reves.
                Vehiculo vehiculo = new Vehiculo(patente, modelo, capacidad, categoria, tipoVehiculo, primerServicio);
                if (campos.length == 7) {
                    vehiculo.agregarTipoServicio(leerEnum(TipoServicio.class, campos[6], "TipoServicio"));
                }
                vehiculos.put(patente, vehiculo);
            } catch (IllegalArgumentException e) {
                advertir(archivo, linea, e.getMessage() + ", se ignora la linea");
            }
        }
        return vehiculos;
    }

    /**
     * Carga los usuarios. Formato de cada linea:
     * nombre;telefono;email[;licenciaConducir;patente;patente;...]
     *
     * Todos los usuarios son clientes. Los que traen licencia y al menos una patente valida
     * ademas quedan dados de alta como conductores: la primera patente es su vehiculo activo
     * y las demas se agregan a sus vehiculos.
     *
     * @param vehiculos los vehiculos ya cargados (resultado de cargarVehiculos)
     */
    public List<Usuario> cargarUsuarios(Map<String, Vehiculo> vehiculos) {
        String archivo = Configuracion.archivoUsuarios();
        List<Usuario> usuarios = new ArrayList<>();
        Map<String, Boolean> emails = new ChainHashMap<>();
        Map<String, Boolean> patentesAsignadas = new ChainHashMap<>();

        for (Linea linea : leerLineas(archivo)) {
            String[] campos = linea.campos;
            if (campos.length < 3) {
                advertir(archivo, linea, "se esperaban al menos 3 campos (nombre;telefono;email), se ignora la linea");
                continue;
            }

            String nombre = campos[0];
            String telefono = campos[1];
            String email = campos[2];
            if (nombre.isEmpty() || email.isEmpty()) {
                advertir(archivo, linea, "el nombre y el email son obligatorios, se ignora la linea");
                continue;
            }
            if (emails.put(email.toLowerCase(), Boolean.TRUE) != null) {
                advertir(archivo, linea, "el email " + email + " esta repetido, se ignora la linea");
                continue;
            }

            Usuario usuario = new Usuario(nombre, telefono, email);
            if (campos.length >= 4) {
                darDeAltaComoConductor(archivo, linea, usuario, vehiculos, patentesAsignadas);
            }
            usuarios.add(usuarios.size(), usuario);
        }
        return usuarios;
    }

    /**
     * Devuelve los problemas encontrados durante la carga, uno por mensaje, con el archivo
     * y el numero de linea. Esta vacía si todo estaba bien.
     */
    public List<String> getAdvertencias() {
        return advertencias;
    }

    // Usa la licencia y las patentes de la linea para convertir al usuario en conductor.
    private void darDeAltaComoConductor(String archivo, Linea linea, Usuario usuario,
                                        Map<String, Vehiculo> vehiculos, Map<String, Boolean> patentesAsignadas) {
        String licencia = linea.campos[3];
        if (licencia.isEmpty()) {
            advertir(archivo, linea, usuario.getNombre() + " tiene la licencia vacia, queda solo como cliente");
            return;
        }

        boolean esConductor = false; // pasa a true con el primer vehiculo valido
        for (int i = 4; i < linea.campos.length; i++) {
            String patente = linea.campos[i];
            Vehiculo vehiculo = vehiculos.get(patente);

            if (vehiculo == null) {
                advertir(archivo, linea, "la patente " + patente + " no existe en el archivo de vehiculos, se ignora");
            } else if (patentesAsignadas.get(patente) != null) {
                advertir(archivo, linea, "la patente " + patente + " ya pertenece a otro conductor, se ignora");
            } else {
                patentesAsignadas.put(patente, Boolean.TRUE);
                if (!esConductor) {
                    usuario.altaConductor(licencia, vehiculo);
                    esConductor = true;
                } else {
                    usuario.getConductor().agregarVehiculo(vehiculo);
                }
            }
        }

        if (!esConductor) {
            String mensaje = usuario.getNombre() + " tiene licencia '" + licencia
                    + "' pero ningun vehiculo valido, queda solo como cliente";
            // Pista: si lo que sigue al ultimo '-' de la licencia es una patente conocida,
            // lo mas probable es que haya un '-' donde iba un ';'.
            String posiblePatente = licencia.substring(licencia.lastIndexOf('-') + 1);
            if (vehiculos.get(posiblePatente) != null) {
                mensaje += " (posible error del archivo: '" + posiblePatente
                        + "' parece una patente pegada a la licencia con '-' en vez de ';')";
            }
            advertir(archivo, linea, mensaje);
        }
    }

    // Lee un archivo del classpath y devuelve solo sus lineas de datos, ya separadas en campos.
    private List<Linea> leerLineas(String nombreArchivo) {
        InputStream entrada = CargadorDatos.class.getClassLoader().getResourceAsStream(nombreArchivo);
        if (entrada == null) {
            throw new IllegalStateException("No se encontro el archivo de datos: " + nombreArchivo);
        }

        List<Linea> lineas = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
            String texto;
            int numero = 0;
            while ((texto = lector.readLine()) != null) {
                numero++;
                if (numero == 1 && texto.startsWith("﻿")) {
                    texto = texto.substring(1); // algunos editores de Windows agregan esta marca al inicio
                }
                texto = texto.trim();
                if (texto.isEmpty() || texto.startsWith("#")) {
                    continue;
                }
                lineas.add(lineas.size(), new Linea(numero, separarCampos(texto)));
            }
        } catch (IOException excepcion) {
            throw new IllegalStateException("No se pudo leer el archivo de datos: " + nombreArchivo, excepcion);
        }
        return lineas;
    }

    // Separa una linea por ';' y le saca los espacios a cada campo.
    // split descarta solos los campos vacios del final, asi que un ';' final no molesta.
    private String[] separarCampos(String texto) {
        String[] campos = texto.split(";");
        for (int i = 0; i < campos.length; i++) {
            campos[i] = campos[i].trim();
        }
        return campos;
    }

    // Convierte un texto a numero decimal. Si no es un numero, lanza un error con un mensaje claro.
    private double leerDecimal(String texto, String campo) {
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " no es un numero: '" + texto + "'");
        }
    }

    // Igual que leerDecimal pero para numeros enteros.
    private int leerEntero(String texto, String campo) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " no es un numero entero: '" + texto + "'");
        }
    }

    // Convierte un texto a un valor de un enum (por ejemplo "auto" -> TipoVehiculo.AUTO).
    // El parametro tipo dice de que enum se trata: TipoVehiculo.class, CategoriaVehiculo.class...
    private <E extends Enum<E>> E leerEnum(Class<E> tipo, String texto, String campo) {
        try {
            return Enum.valueOf(tipo, texto.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(campo + " invalido: '" + texto + "'");
        }
    }

    private void advertir(String archivo, Linea linea, String mensaje) {
        advertencias.add(advertencias.size(), archivo + ", linea " + linea.numero + ": " + mensaje);
    }
}
