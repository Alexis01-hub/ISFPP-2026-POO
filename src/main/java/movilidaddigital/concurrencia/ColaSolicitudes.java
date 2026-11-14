package movilidaddigital.concurrencia;

import movilidaddigital.modelo.Usuario;
import movilidaddigital.negocio.Alternativa;
import movilidaddigital.negocio.Despachador;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Buffer acotado de solicitudes implementado con una lista.
 *
 * <p>Los consumidores recorren el buffer y retiran la primera solicitud
 * compatible con su conductor. El acceso a la lista se protege con un lock,
 * sin utilizar métodos {@code synchronized}.</p>
 */
public final class ColaSolicitudes {
    public static final class Solicitud {
        private final long numero;
        private final Usuario cliente;
        private final Alternativa alternativa;

        private Solicitud(long numero, Usuario cliente, Alternativa alternativa) {
            this.numero = numero;
            this.cliente = cliente;
            this.alternativa = alternativa;
        }

        public static Solicitud viaje(long numero, Usuario cliente, Alternativa alternativa) {
            return new Solicitud(numero, cliente, alternativa);
        }

        public long getNumero() {
            return numero;
        }

        public Usuario getCliente() {
            return cliente;
        }

        public Alternativa getAlternativa() {
            return alternativa;
        }
    }

    private final Lock lock = new ReentrantLock();
    private final Condition haySolicitud = lock.newCondition();
    private final Condition hayEspacio = lock.newCondition();
    private final List<Solicitud> solicitudes = new ArrayList<>();
    private final int capacidad;
    private boolean cerrada;

    public ColaSolicitudes(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero");
        }
        this.capacidad = capacidad;
    }

    /**
     * Agrega una solicitud al buffer, esperando si no hay espacio.
     *
     * @param solicitud solicitud generada por el productor
     * @throws InterruptedException si el productor es interrumpido
     */
    public void publicar(Solicitud solicitud) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (solicitudes.size() >= capacidad && !cerrada) {
                hayEspacio.await();
            }
            if (cerrada) {
                throw new IllegalStateException("La cola de solicitudes está cerrada");
            }
            solicitudes.add(solicitud);
            haySolicitud.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Busca y retira la primera solicitud compatible con el conductor.
     *
     * @param conductor conductor consumidor
     * @param despachador componente que verifica compatibilidad
     * @return una solicitud compatible o {@code null} cuando la cola está
     * cerrada y vacía
     * @throws InterruptedException si el consumidor es interrumpido
     */
    public Solicitud tomar(Usuario conductor, Despachador despachador)
            throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (true) {
                for (int i = 0; i < solicitudes.size(); i++) {
                    Solicitud solicitud = solicitudes.get(i);
                    if (despachador.esCompatible(conductor.getConductor(),
                            solicitud.getAlternativa().getServicio())) {
                        solicitudes.remove(i);
                        hayEspacio.signal();
                        return solicitud;
                    }
                }
                if (cerrada && solicitudes.isEmpty()) {
                    return null;
                }
                haySolicitud.await();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Cierra el buffer después de que el productor publicó todas las solicitudes.
     */
    public void cerrar() {
        lock.lock();
        try {
            cerrada = true;
            haySolicitud.signalAll();
            hayEspacio.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
