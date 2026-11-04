package movilidaddigital.datos;

import java.util.ArrayList;
import java.util.List;
import movilidaddigital.datos.dao.DAOFactory;
import movilidaddigital.datos.dao.DAOLoader;
import movilidaddigital.datos.dto.ServicioDTO;
import movilidaddigital.datos.dto.UsuarioDTO;
import movilidaddigital.datos.dto.VehiculoDTO;
import movilidaddigital.datastructures.ChainHashMap;
import movilidaddigital.datastructures.Map;
import movilidaddigital.modelo.Servicio;
import movilidaddigital.modelo.Usuario;
import movilidaddigital.modelo.Vehiculo;
import movilidaddigital.modelo.enums.CategoriaVehiculo;
import movilidaddigital.modelo.enums.TipoServicio;
import movilidaddigital.modelo.enums.TipoVehiculo;

/** Adapta los DTO obtenidos por JDBC al modelo usado por la capa de negocio. */
public class CargadorDatosBD {
    private final DAOFactory fabrica;

    public CargadorDatosBD() {
        this.fabrica = DAOLoader.getFactory("BD");
    }

    public List<Servicio> cargarServicios() {
        try {
            List<Servicio> resultado = new ArrayList<>();
            for (ServicioDTO dto : fabrica.getServicioDAO().obtenerTodos()) {
                resultado.add(new Servicio(dto.getNombre(), dto.getTarifaBase(),
                        dto.getPrecioKm(), dto.getPrecioMinuto(),
                        TipoVehiculo.desdeCodigo(dto.getCodigoTipoVehiculo()),
                        CategoriaVehiculo.desdeCodigo(dto.getCodigoCategoriaVehiculo()),
                        TipoServicio.desdeCodigo(dto.getCodigoTipoServicio())));
            }
            return resultado;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudieron cargar los servicios desde la BD", e);
        }
    }

    public Map<String, Vehiculo> cargarVehiculos() {
        try {
            Map<String, Vehiculo> resultado = new ChainHashMap<>();
            for (VehiculoDTO dto : fabrica.getVehiculoDAO().obtenerTodos()) {
                List<TipoServicio> tipos = tiposServicio(dto.getTiposServicio());
                Vehiculo vehiculo = new Vehiculo(dto.getPatente(), dto.getModelo(),
                        dto.getCapacidadPasajeros(),
                        TipoVehiculo.desdeCodigo(dto.getCodigoTipoVehiculo()),
                        CategoriaVehiculo.desdeCodigo(dto.getCodigoCategoriaVehiculo()),
                        tipos.get(0));
                for (int i = 1; i < tipos.size(); i++) {
                    vehiculo.agregarTipoServicio(tipos.get(i));
                }
                resultado.put(dto.getPatente(), vehiculo);
            }
            return resultado;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudieron cargar los vehículos desde la BD", e);
        }
    }

    public List<Usuario> cargarUsuarios(Map<String, Vehiculo> vehiculos) {
        try {
            List<Usuario> resultado = new ArrayList<>();
            for (UsuarioDTO dto : fabrica.getUsuarioDAO().obtenerTodos()) {
                Usuario usuario = new Usuario(dto.getNombre(), dto.getTelefono(), dto.getEmail());
                String licencia = dto.getLicenciaConducir();
                String patentes = dto.getPatentes();
                if (licencia != null && !licencia.isBlank() && patentes != null) {
                    String[] lista = patentes.split("[,;]");
                    Vehiculo primero = vehiculos.get(lista[0].trim());
                    if (primero != null) {
                        usuario.altaConductor(licencia, primero);
                        for (int i = 1; i < lista.length; i++) {
                            Vehiculo adicional = vehiculos.get(lista[i].trim());
                            if (adicional != null) {
                                usuario.getConductor().agregarVehiculo(adicional);
                            }
                        }
                    }
                }
                resultado.add(usuario);
            }
            return resultado;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudieron cargar los usuarios desde la BD", e);
        }
    }

    private static <E extends Enum<E>> E enumValue(Class<E> tipo, String valor) {
        return Enum.valueOf(tipo, valor.trim().toUpperCase());
    }

    private static List<TipoServicio> tiposServicio(String valor) {
        String normalizado = valor.trim();
        try {
            int codigo = Integer.parseInt(normalizado);
            if (codigo == 1) {
                return List.of(TipoServicio.PASAJEROS);
            }
            if (codigo == 2) {
                return List.of(TipoServicio.ENVIOS);
            }
            if (codigo == 3) {
                return List.of(TipoServicio.PASAJEROS, TipoServicio.ENVIOS);
            }
            return List.of(TipoServicio.desdeCodigo(codigo));
        } catch (NumberFormatException e) {
            String[] valores = normalizado.split("[,;]");
            List<TipoServicio> tipos = new ArrayList<>();
            for (String valorTipo : valores) {
                tipos.add(enumValue(TipoServicio.class, valorTipo));
            }
            return tipos;
        }
    }
}
