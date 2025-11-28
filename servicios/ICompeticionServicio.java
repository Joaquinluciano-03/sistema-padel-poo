package servicios;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import modelo.Cancha;
import modelo.Partido;
import modelo.Sede;
import modelo.Torneo;

public interface ICompeticionServicio {
    
    // Gestión de Sedes
    void agregarSede(Sede sede) throws IllegalArgumentException; 
    Sede buscarSedePorNombre(String nombre);
    List<Sede> getSedes();
    boolean modificarSede(String nombreActual, String nuevoNombre, String nuevaDireccion);
    boolean eliminarSede(String nombre); 
    
    // Gestión de Canchas
    boolean modificarCancha(String nombreSede, int numeroCancha, String nuevaSuperficie, boolean nuevaIluminacion);
    boolean eliminarCancha(Sede sede, int numeroCancha); 

    // Gestión de Torneos y Partidos
    void registrarTorneo(Torneo torneo);
    void registrarPartidoSuelto(Partido partido);
    Torneo buscarTorneoPorNombre(String nombre);
    List<Torneo> getTodosLosTorneos();
    List<Partido> getTodosLosPartidos();
    List<Partido> getPartidosPendientes();
    boolean eliminarTorneo(Torneo torneo);
    boolean eliminarPartido(Partido partido);

    List<Partido> getPartidosPorSedeYFecha(String nombreSede, LocalDate fecha);
    
    boolean validarDisponibilidadCancha(Cancha cancha, LocalDateTime inicio, int duracionMinutos);

    List<Partido> getPartidosSuetos();
    void setPartidosSuetos(List<Partido> partidos);
}