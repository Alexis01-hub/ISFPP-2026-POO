package movilidaddigital.concurrencia;

import movilidaddigital.modelo.Usuario;
import movilidaddigital.modelo.CalificacionViaje;
import movilidaddigital.negocio.GestorViajes;
import movilidaddigital.negocio.Despachador;

import java.time.LocalDateTime;

/**
 * Representa un consumidor de solicitudes de viaje. Cada conductor tiene su propio hilo que ejecuta este consumidor.
 * Cada consumidor retira solicitudes independientes del buffer compartido.
 */
public final class ConsumidorSolicitudes implements Runnable {
    private final ColaSolicitudes cola;
    private final GestorViajes gestorViajes;
    private final Despachador despachador;
    private final Usuario conductor;

    public ConsumidorSolicitudes(ColaSolicitudes cola, GestorViajes gestorViajes,
                                 Despachador despachador, Usuario conductor) {
        this.cola = cola;
        this.gestorViajes = gestorViajes;
        this.despachador = despachador;
        this.conductor = conductor;
    }

    @Override
    public void run() {
        try {
            while (true) {
                ColaSolicitudes.Solicitud solicitud = cola.tomar(conductor, despachador);
                if (solicitud == null) return;
                var viaje = gestorViajes.solicitar(solicitud.getCliente(),
                        solicitud.getAlternativa(), LocalDateTime.now());
                gestorViajes.aceptar(viaje, conductor, LocalDateTime.now());
                System.out.printf("[CONDUCTOR] %s aceptó la solicitud #%d.%n",
                        conductor.getNombre(), solicitud.getNumero());
                gestorViajes.iniciar(viaje, LocalDateTime.now());
                gestorViajes.finalizar(viaje, LocalDateTime.now(),
                        CalificacionViaje.BUENO, CalificacionViaje.BUENO);
                System.out.printf("[CONDUCTOR] %s finalizó la solicitud #%d y quedó disponible.%n",
                        conductor.getNombre(), solicitud.getNumero());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[CONSUMIDOR] Interrumpido.");
        }
    }
}
