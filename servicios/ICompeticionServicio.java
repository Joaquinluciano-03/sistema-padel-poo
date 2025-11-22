package servicios;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import modelo.Cancha;
import modelo.Partido;
import modelo.Sede;
import modelo.Torneo;

/**
 * Interfaz que define el contrato para los servicios de gestión de competiciones,
 * incluyendo torneos, partidos, sedes y la validación de disponibilidad de cancha.
 * Cumple con el Principio de Inversión de Dependencias (DIP).
 */
public interface ICompeticionServicio {
    
    // Gestión de Sedes
    void agregarSede(Sede sede);
    Sede buscarSedePorNombre(String nombre);
    List<Sede> getSedes();
    boolean modificarSede(String nombreActual, String nuevoNombre, String nuevaDireccion);
    boolean modificarCancha(String nombreSede, int numeroCancha, String nuevaSuperficie, boolean nuevaIluminacion);

    // Gestión de Torneos y Partidos
    void registrarTorneo(Torneo torneo);
    void registrarPartidoSuelto(Partido partido);
    Torneo buscarTorneoPorNombre(String nombre);
    List<Torneo> getTodosLosTorneos();
    List<Partido> getTodosLosPartidos();
    List<Partido> getPartidosPendientes();
    boolean eliminarTorneo(Torneo torneo);
    boolean eliminarPartido(Partido partido);

    // Validación de Horarios (Método crucial)
    List<Partido> getPartidosPorSedeYFecha(String nombreSede, LocalDate fecha);
    
    /**
     * Verifica si una cancha está disponible en un rango de tiempo específico.
     * Esta funcionalidad será implementada por el Validador Horario.
     */
    boolean validarDisponibilidadCancha(Cancha cancha, LocalDateTime inicio, int duracionMinutos);

    // --- NUEVOS CONTRATOS PARA PERSISTENCIA ---
    List<Partido> getPartidosSuetos();
    void setPartidosSuetos(List<Partido> partidos);
}