package movilidaddigital.modelo;

import movilidaddigital.modelo.enums.EstadoViaje;

import java.time.LocalDateTime;

public class RegistroViaje {
    private LocalDateTime fechaHora;
    private EstadoViaje estadoViaje;

    public RegistroViaje(LocalDateTime fechaHora, EstadoViaje estadoViaje) {
        this.fechaHora = fechaHora;
        this.estadoViaje = estadoViaje;
    }

    public EstadoViaje getEstadoViaje() {
        return estadoViaje;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}
