package movilidaddigital.modelo;

import movilidaddigital.excepciones.TransicionViajeInvalidaException;
import movilidaddigital.modelo.enums.CalificacionViaje;
import movilidaddigital.modelo.enums.EstadoViaje;
import movilidaddigital.modelo.enums.RolUsuario;

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
     * El cliente pide el viaje. queda SOLICITADO y se agrega a la lista de viajes del cliente.
     * @param fechaHora fecha y hora en que el cliente solicita el viaje
     */
    public void solicitar(LocalDateTime fechaHora){
        if(estadoActual() != null){
            throw new TransicionViajeInvalidaException("El viaje ya fue solicitado");
        }
        registrar(fechaHora, EstadoViaje.SOLICITADO);
        cliente.getCliente().agregarViaje(this);
    }

    /**
     * El conductor acepta el viaje (solo si esta SOLICITADO)
     * El viaje usa el vehiculo activo del conductor y se agrega a la lista de viajes del conductor.
     * @param fechaHora fecha y hora en que el conductor acepta el viaje
     * @param conductor usuario que acepta el viaje. Debe ser un conductor.
     */
    public void aceptar(LocalDateTime fechaHora, Usuario conductor){
        if (estadoActual() != EstadoViaje.SOLICITADO){
            throw new TransicionViajeInvalidaException("Solo se puede aceptar un viaje SOLICITADO");
        }
        if (conductor.getConductor() == null){
            throw new TransicionViajeInvalidaException("El usuario no es conductor");
        }
        this.conductor = conductor;
        this.vehiculo = conductor.getConductor().getVehiculoActivo();
        registrar(fechaHora, EstadoViaje.ACEPTADO);
        conductor.getConductor().agregarViaje(this);
    }

    /**
     * El pasajero sube al vehiculo (o se entrega la carga). Solo si esta ACEPTADO.
     * @param fechaHora fecha y hora en que el viaje inicia
     */
    public void iniciar(LocalDateTime fechaHora){
        if (estadoActual() != EstadoViaje.ACEPTADO){
            throw new TransicionViajeInvalidaException("Solo se puede iniciar un viaje ACEPTADO");
        }
        registrar(fechaHora, EstadoViaje.INICIADO);
    }

    /**
     * El viaje llega al destino. Solo si esta INICIADO.
     * Cada uno califica al otro.
     * @param fechaHora fecha y hora en que el viaje finaliza
     * @param calificacionConductor calificacion que recibe el conductor por parte del cliente
     * @param calificacionCliente calificacion que recibe el cliente por parte del conductor
     */
    public void finalizar(LocalDateTime fechaHora, CalificacionViaje calificacionConductor, CalificacionViaje calificacionCliente){
        if (estadoActual() != EstadoViaje.INICIADO){
            throw new TransicionViajeInvalidaException("Solo se puede finalizar un viaje INICIADO");
        }
        this.calificacionConductor = calificacionConductor;
        this.calificacionCliente = calificacionCliente;
        registrar(fechaHora, EstadoViaje.FINALIZADO);
    }

    /**
     * Cancela el viaje. Lo puede hacer el cliente o el conductor.
     * salvo que el viaje ya haya terminado
     * queda guardado quien cancelo y el motivo. nadie califica.
     * @param fechaHora fecha y hora en que se cancela el viaje
     * @param usuario usuario que cancela el viaje. Debe ser el cliente o el conductor.
     * @param motivo motivo de la cancelacion. No puede ser nulo ni vacio.
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
    }

    /**
     * Ningun conductor acepto a tiempo: el viaje pasa a rechazado. Solo si esta SOLICITADO.
     * @param fechaHora fecha y hora en que el viaje es rechazado
     */
    public void rechazar(LocalDateTime fechaHora){
        if  (estadoActual() != EstadoViaje.SOLICITADO){
            throw new TransicionViajeInvalidaException("Solo se puede rechazar un viaje SOLICITADO");
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
