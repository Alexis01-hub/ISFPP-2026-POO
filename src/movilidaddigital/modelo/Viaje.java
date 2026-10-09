package movilidaddigital.modelo;

import movilidaddigital.excepciones.TransicionViajeInvalidaException;
import movilidaddigital.modelo.enums.CalificacionViaje;
import movilidaddigital.modelo.enums.EstadoViaje;
import movilidaddigital.modelo.enums.RolUsuario;
import movilidaddigital.excepciones.VehiculoNoCompatibleException;
import movilidaddigital.modelo.enums.EstadoConductor;
import java.time.LocalDateTime;
import java.util.*;

public class Viaje {
    private UUID id;
    private String motivoCancelacion;
    private Usuario cliente;
    private Usuario conductor;
    private Vehiculo vehiculo;
    private Ubicacion origen;
    private Ubicacion destino;
    private Servicio servicio;
    private List<RegistroViaje> registroViaje;
    private CalificacionViaje calificacionConductor;
    private CalificacionViaje calificacionCliente;
    private RolUsuario rolCancela; // Indica si la cancelación fue por parte del cliente o del conductor

    public Viaje(Usuario cliente, Ubicacion origen, Ubicacion destino, Servicio servicio) {
        this.id = UUID.randomUUID();
        this.cliente = cliente;
        this.origen = origen;
        this.destino = destino;
        this.servicio = servicio;
        this.registroViaje = new ArrayList<>();
        this.calificacionConductor = CalificacionViaje.NO_CALIFICADO;
        this.calificacionCliente = CalificacionViaje.NO_CALIFICADO;
    }

    /**
     * Solicita el viaje, registrando la fecha y hora en que se solicita y pasando a estado SOLICITADO.
     * @param fechaHora fecha y hora en que se solicita el viaje
     */
    public void solicitar(LocalDateTime fechaHora) {
        if (estadoActual() != null) {
            throw new TransicionViajeInvalidaException(
                    "El viaje ya fue solicitado");
        }

        if (cliente.getCliente().enViaje()) {
            throw new TransicionViajeInvalidaException(
                    "El cliente ya tiene un viaje activo");
        }

        registrar(fechaHora, EstadoViaje.SOLICITADO);
        cliente.getCliente().agregarViaje(this);
    }

    /**
     * Acepta el viaje que se encuentra en estado SOLICITADO, pasando a estado ACEPTADO.
     * @param fechaHora fecha y hora en que se acepta el viaje
     * @param conductor usuario que acepta el viaje (debe ser un conductor)
     */
    public void aceptar(LocalDateTime fechaHora, Usuario conductor) {
        if (estadoActual() != EstadoViaje.SOLICITADO) {
            throw new TransicionViajeInvalidaException(
                    "Solo se puede aceptar un viaje SOLICITADO");
        }

        if (conductor.getConductor() == null) {
            throw new TransicionViajeInvalidaException(
                    "El usuario no es conductor");
        }

        Vehiculo vehiculoConductor =
                conductor.getConductor().getVehiculoActivo();

        if (vehiculoConductor.getTipoVehiculo() != servicio.getTipoVehiculo()
                || vehiculoConductor.getCategoriaVehiculo().getValor()
                < servicio.getCategoriaVehiculo().getValor()
                || !vehiculoConductor.getTipoServicios()
                .contains(servicio.getTipoServicio())) {
            throw new VehiculoNoCompatibleException(
                    "El vehículo no es compatible con el servicio");
        }

        this.conductor = conductor;
        this.vehiculo = vehiculoConductor;
        registrar(fechaHora, EstadoViaje.ACEPTADO);
        conductor.getConductor().agregarViaje(this);
        conductor.getConductor().setEstado(
                EstadoConductor.VIAJE_A_ORIGEN);
    }

    /**
     * Inicia el viaje que se encuentra en estado ACEPTADO, pasando a estado INICIADO.
     * @param fechaHora fecha y hora en que se inicia el viaje
     */
    public void iniciar(LocalDateTime fechaHora) {
        if (estadoActual() != EstadoViaje.ACEPTADO) {
            throw new TransicionViajeInvalidaException(
                    "Solo se puede iniciar un viaje ACEPTADO");
        }

        registrar(fechaHora, EstadoViaje.INICIADO);
        conductor.getConductor().setEstado(
                EstadoConductor.VIAJE_A_DESTINO);
    }

    /**
     * finaliza el viaje que se encuentra en estado INICIADO, pasando a estado FINALIZADO.
     * @param fechaHora fecha y hora en que se finaliza el viaje
     * @param calificacionCliente calificacion del cliente sobre el viaje
     * @param calificacionConductor calificacion del conductor sobre el viaje
     */
    public void finalizar(LocalDateTime fechaHora,
                          CalificacionViaje calificacionCliente,
                          CalificacionViaje calificacionConductor) {
        if (estadoActual() != EstadoViaje.INICIADO) {
            throw new TransicionViajeInvalidaException(
                    "Solo se puede finalizar un viaje INICIADO");
        }

        this.calificacionCliente = calificacionCliente;
        this.calificacionConductor = calificacionConductor;
        registrar(fechaHora, EstadoViaje.FINALIZADO);

        conductor.getConductor().setEstado(
                EstadoConductor.DISPONIBLE);
    }

    /**
     * Cancela el viaje que se encuentra en estado SOLICITADO, ACEPTADO o INICIADO, pasando a estado CANCELADO.
     * @param fechaHora fecha y hora en que se cancela el viaje
     * @param usuario usuario que cancela el viaje (debe ser el cliente o el conductor)
     * @param motivo motivo de la cancelacion en texto libre
     */
    public void cancelar(LocalDateTime fechaHora, Usuario usuario, String motivo){
        EstadoViaje estado = estadoActual();
        if(estado == null || estado == EstadoViaje.FINALIZADO || estado == EstadoViaje.CANCELADO || estado == EstadoViaje.RECHAZADO){
            throw new TransicionViajeInvalidaException("No se puede cancelar un viaje en estado "+ estado);
        }
        if (motivo == null || motivo.isBlank()){
            throw new IllegalArgumentException("El motivo de cancelacion no puede ser nulo o vacio");
        }
        if (usuario == cliente){
            this.rolCancela = RolUsuario.CLIENTE;
        } else if (usuario == conductor){
            this.rolCancela = RolUsuario.CONDUCTOR;
        } else {
            throw new TransicionViajeInvalidaException("El usuario que cancela no es ni el cliente ni el conductor del viaje");
        }
        this.motivoCancelacion = motivo;
        registrar(fechaHora, EstadoViaje.CANCELADO);

        if (conductor != null) {
            if (rolCancela == RolUsuario.CLIENTE) {
                conductor.getConductor().setEstado(EstadoConductor.DISPONIBLE);
            } else {
                conductor.getConductor().setEstado(EstadoConductor.FUERA_DE_SERVICIO);
            }
        }
    }

    /**
     * rechaza el viaje que se encuentra en estado SOLICITADO, pasando a estado RECHAZADO.
     *
     * @param fechaHora fecha y hora en que se rechaza el viaje
     */
    public void rechazar(LocalDateTime fechaHora) {
        if (estadoActual() != EstadoViaje.SOLICITADO) {
            throw new TransicionViajeInvalidaException(
                    "Solo se puede rechazar un viaje SOLICITADO");
        }

        registrar(fechaHora, EstadoViaje.RECHAZADO);
    }

    /**
     * Devuelve el estado del ultimo registro del historial,
     * o null si el viaje todavia no fue solicitado.
     * @return estado del viaje
     */
    public EstadoViaje estadoActual(){
        if (registroViaje.isEmpty()){
            return null;
        }
        return registroViaje.get(registroViaje.size()-1).getEstadoViaje();
    }

    // agrega una linea al historial de estados del viaje. Se usa para registrar cada cambio de estado.
    private void registrar(LocalDateTime fechaHora, EstadoViaje estado) {
        registroViaje.add(new RegistroViaje(fechaHora, estado));
    }

    public UUID getId() {
        return id;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public Usuario getConductor() {
        return conductor;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public Ubicacion getOrigen() {
        return origen;
    }

    public Ubicacion getDestino() {
        return destino;
    }

    public Servicio getServicio() {
        return servicio;
    }

    /**
     * Devuelve una lista inmodificable con el historial de estados del viaje.
     * @return lista inmodificable de registros de viaje
     */
    public List<RegistroViaje> getRegistroViaje() {
        return Collections.unmodifiableList(registroViaje);
    }

    public CalificacionViaje getCalificacionConductor() {
        return calificacionConductor;
    }

    public CalificacionViaje getCalificacionCliente() {
        return calificacionCliente;
    }

    public RolUsuario getRolCancela() {
        return rolCancela;
    }
}
