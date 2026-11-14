package movilidaddigital.concurrencia;

import movilidaddigital.modelo.Usuario;
import movilidaddigital.negocio.Alternativa;
import java.util.List;
/**
 * Representa un productor de solicitudes de viaje. Este productor genera solicitudes de viaje para los clientes
 * y las publica en la cola de solicitudes para que los consumidores (conductores) puedan competir por ellas.
 */
public final class ProductorSolicitudes implements Runnable {
    private final ColaSolicitudes cola;
    private final List<Usuario> clientes;
    private final Alternativa alternativa;
    private final int cantidadClientes;

    public ProductorSolicitudes(ColaSolicitudes cola, List<Usuario> clientes, Alternativa alternativa,
                                int cantidadClientes) {
        this.cola = cola;
        this.clientes = clientes;
        this.alternativa = alternativa;
        this.cantidadClientes = cantidadClientes;
    }

    @Override
    public void run() {
        try {
            for (int numero = 1; numero <= cantidadClientes; numero++) {
                Usuario cliente = clientes.get(numero - 1);
                cola.publicar(ColaSolicitudes.Solicitud.viaje(numero, cliente, alternativa));
                System.out.printf("[PRODUCTOR] Cliente #%d solicitó un viaje: %s -> %s.%n",
                        numero, alternativa.getOrigen(), alternativa.getDestino());
            }
            cola.cerrar();
            System.out.printf("[PRODUCTOR] No quedan más solicitudes (%d clientes simulados).%n",
                    cantidadClientes);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[PRODUCTOR] Interrumpido.");
        }
    }
}
