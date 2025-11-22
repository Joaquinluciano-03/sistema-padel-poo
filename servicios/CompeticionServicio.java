package servicios;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import modelo.Cancha;
import modelo.Partido;
import modelo.Sede;
import modelo.Torneo;
import util.ValidadorHorario;

/**
 * Clase de servicio responsable de la gestión de competiciones (Torneos, Partidos, Sedes).
 * Implementa ICompeticionServicio para cumplir el DIP y utiliza ValidadorHorario para cumplir SRP.
 */
public class CompeticionServicio implements ICompeticionServicio {
    
    private List<Sede> sedes = new ArrayList<>();
    private List<Partido> partidosSuetos = new ArrayList<>(); // <-- ESTA LISTA DEBE SER PERSISTIDA
    private List<Torneo> torneosRegistrados = new ArrayList<>();
    
    private final ValidadorHorario validadorHorario;

    public CompeticionServicio() {
        this.validadorHorario = new ValidadorHorario(); 
    }

    @Override
    public void agregarSede(Sede sede) {
        sedes.add(sede);
    }
    
    @Override
    public void registrarTorneo(Torneo torneo) {
        torneosRegistrados.add(torneo);
    }

    @Override
    public void registrarPartidoSuelto(Partido partido) {
        if (partido.getTorneoPerteneciente() == null) {
            partidosSuetos.add(partido);
        } else {
             partido.getTorneoPerteneciente().agregarPartido(partido);
        }
    }

    @Override
    public List<Sede> getSedes() { return sedes; }

    @Override
    public Sede buscarSedePorNombre(String nombre) {
        return sedes.stream().filter(s -> s.getNombre().equalsIgnoreCase(nombre)).findFirst().orElse(null);
    }
    
    @Override
    public List<Torneo> getTodosLosTorneos() { return torneosRegistrados; }

    @Override
    public Torneo buscarTorneoPorNombre(String nombre) {
        return torneosRegistrados.stream().filter(t -> t.getNombre().equalsIgnoreCase(nombre)).findFirst().orElse(null);
    }
    
    @Override
    public List<Partido> getTodosLosPartidos() {
        List<Partido> todos = new ArrayList<>(partidosSuetos);
        torneosRegistrados.forEach(torneo -> todos.addAll(torneo.getPartidos()));
        return todos;
    }

    @Override
    public List<Partido> getPartidosPendientes() {
        return getTodosLosPartidos().stream().filter(p -> !p.isFinalizado()).collect(Collectors.toList());
    }
    
    @Override
    public List<Partido> getPartidosPorSedeYFecha(String nombreSede, LocalDate fecha) {
        Sede sede = buscarSedePorNombre(nombreSede);
        if (sede == null) return new ArrayList<>();
        
        List<Cancha> canchasDeSede = sede.getCanchas();
        
        return getTodosLosPartidos().stream()
            .filter(p -> p.getCancha() != null && p.getFechaHora().toLocalDate().isEqual(fecha))
            .filter(p -> canchasDeSede.stream().anyMatch(c -> c.getNumero() == p.getCancha().getNumero())) 
            .collect(Collectors.toList());
    }

    @Override
    public boolean validarDisponibilidadCancha(Cancha cancha, LocalDateTime inicio, int duracionMinutos) {
        Sede sede = buscarSedePorCancha(cancha);
        if (sede == null) return false;
        
        List<Partido> partidosDelDia = getPartidosPorSedeYFecha(
            sede.getNombre(), 
            inicio.toLocalDate());
            
        return validadorHorario.validarDisponibilidadCancha(cancha, inicio, duracionMinutos, partidosDelDia);
    }
    
    private Sede buscarSedePorCancha(Cancha cancha) {
        for (Sede sede : sedes) {
            if (sede.getCanchas().stream().anyMatch(c -> c.getNumero() == cancha.getNumero())) {
                return sede;
            }
        }
        return null;
    }

    @Override
    public boolean eliminarTorneo(Torneo torneo) { return torneosRegistrados.remove(torneo); }

    @Override
    public boolean eliminarPartido(Partido partido) {
        if (partidosSuetos.remove(partido)) return true;
        for (Torneo torneo : torneosRegistrados) {
            if (torneo.getPartidos().remove(partido)) return true;
        }
        return false;
    }
    
    @Override
    public boolean modificarSede(String nombreActual, String nuevoNombre, String nuevaDireccion) {
        Sede sede = buscarSedePorNombre(nombreActual);
        if (sede == null) return false;
        sede.setNombre(nuevoNombre);
        sede.setDireccion(nuevaDireccion);
        return true;
    }

    @Override
    public boolean modificarCancha(String nombreSede, int numeroCancha, String nuevaSuperficie, boolean nuevaIluminacion) {
        Sede sede = buscarSedePorNombre(nombreSede);
        if (sede == null) return false;
        Cancha cancha = sede.getCanchas().stream().filter(c -> c.getNumero() == numeroCancha).findFirst().orElse(null);
        if (cancha == null) return false;
        cancha.setTipoSuperficie(nuevaSuperficie);
        cancha.setIluminacion(nuevaIluminacion);
        return true;
    }
    
    // --- MÉTODOS DE PERSISTENCIA ---
    @Override
    public List<Partido> getPartidosSuetos() {
        return partidosSuetos;
    }

    @Override
    public void setPartidosSuetos(List<Partido> partidos) {
        // Limpiar la lista existente y añadir todos los elementos cargados
        this.partidosSuetos.clear();
        this.partidosSuetos.addAll(partidos);
    }
}