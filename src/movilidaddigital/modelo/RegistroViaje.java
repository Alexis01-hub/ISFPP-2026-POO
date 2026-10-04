package movilidaddigital.modelo;

import movilidaddigital.modelo.enums.EstadoViaje;

import java.time.LocalDate;

public class RegistroViaje {
    private LocalDate fechaHora;
    private EstadoViaje estadoViaje;

    public RegistroViaje(LocalDate fechaHora, EstadoViaje estadoViaje) {
        this.fechaHora = fechaHora;
        this.estadoViaje = estadoViaje;
    }

    public EstadoViaje getEstadoViaje() {
        return estadoViaje;
    }

    public LocalDate getFechaHora() {
        return fechaHora;
    }
}
