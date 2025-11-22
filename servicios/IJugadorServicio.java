package servicios;

import excepciones.JugadorYaExisteException;
import java.util.List;
import modelo.Jugador;

/**
 * Interfaz que define el contrato exclusivo para los servicios de gestión de Jugadores.
 * Cumple con el Principio de Segregación de Interfaces (ISP).
 */
public interface IJugadorServicio {
    
    void registrar(Jugador jugador) throws JugadorYaExisteException;
    
    boolean modificar(String dni, String nuevoNombre, String nuevoApellido, String nuevaPosicion, int nuevoNivel);
    
    boolean eliminar(String dni);
    
    Jugador buscarPorDni(String dni);
    
    List<Jugador> getTodos();
    
    // Los métodos para Arbitro han sido eliminados de esta interfaz.
}