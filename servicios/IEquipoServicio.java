package servicios;

import excepciones.EquipoYaExisteException;
import java.util.List;
import modelo.Equipo;
import modelo.Jugador;

/**
 * Interfaz que define el contrato para los servicios de gestión de equipos.
 * Cumple con el Principio de Inversión de Dependencias (DIP).
 */
public interface IEquipoServicio {
    
    void registrar(Equipo equipo) throws EquipoYaExisteException;
    
    void editar(String nombreActual, String nuevoNombre, List<Jugador> nuevosJugadores) throws EquipoYaExisteException, IllegalArgumentException;
    
    boolean eliminar(String nombre);
    
    Equipo buscarPorNombre(String nombre);
    
    List<Equipo> getTodos();
}