package controlador;

import excepciones.EquipoYaExisteException;
import excepciones.InscripcionException;
import excepciones.JugadorYaExisteException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import modelo.Arbitro;
import modelo.Cancha;
import modelo.Equipo;
import modelo.Jugador;
import modelo.Partido;
import modelo.Sede;
import modelo.Torneo;
import persistencia.IPersistencia;
import servicios.ArbitroServicio;
import servicios.CompeticionServicio;
import servicios.EquipoServicio;
import servicios.IArbitroServicio;
import servicios.ICompeticionServicio;
import servicios.IEquipoServicio;
import servicios.IJugadorServicio;
import servicios.JugadorServicio;

/**
 * Actúa como una fachada (Facade) para la lógica de negocio del sistema.
 * Depende de todas las interfaces de servicio (IJugadorServicio, IArbitroServicio, etc.)
 * para cumplir con el Principio de Inversión de Dependencias (DIP).
 */
public class GestorSistema {
    
    private IPersistencia persistencia; 
    
    private IJugadorServicio jugadorServicio; 
    private IArbitroServicio arbitroServicio; 
    private IEquipoServicio equipoServicio; 
    private ICompeticionServicio competicionServicio; // <-- Variable correcta

    public GestorSistema() {
        this.jugadorServicio = new JugadorServicio();
        this.arbitroServicio = new ArbitroServicio();
        this.equipoServicio = new EquipoServicio();
        this.competicionServicio = new CompeticionServicio();
    }
    
    public void setPersistencia(IPersistencia persistencia) {
        this.persistencia = persistencia;
    }

    private void guardar() {
        if (persistencia != null) {
            persistencia.guardarDatos(this);
        }
    }
    
    // --- MÉTODOS DE REGISTRO / AGREGAR ---
    
    public void agregarSede(Sede sede) {
        competicionServicio.agregarSede(sede);
        guardar();
    }

    public void registrarTorneo(Torneo torneo) {
        competicionServicio.registrarTorneo(torneo);
        guardar();
    }
    
    public void registrarJugador(Jugador jugador) throws JugadorYaExisteException {
        // Validación cruzada para DNI único:
        if (arbitroServicio.buscarPorDni(jugador.getDni()) != null) {
             throw new JugadorYaExisteException("El DNI ya está registrado como árbitro.", jugador.getDni());
        }
        jugadorServicio.registrar(jugador);
        guardar();
    }
    
    public void registrarArbitro(Arbitro arbitro) throws JugadorYaExisteException {
        // Validación cruzada para DNI único:
        if (jugadorServicio.buscarPorDni(arbitro.getDni()) != null) {
             throw new JugadorYaExisteException("El DNI ya está registrado como jugador.", arbitro.getDni());
        }
        arbitroServicio.registrar(arbitro); 
        guardar();
    }
    
    public void registrarEquipo(Equipo equipo) throws EquipoYaExisteException {
        equipoServicio.registrar(equipo);
        guardar();
    }
    
    public void registrarPartidoSuelto(Partido partido) {
        competicionServicio.registrarPartidoSuelto(partido);
        guardar();
    }

    public void inscribirEquipoEnTorneo(Torneo torneo, Equipo equipo) throws InscripcionException {
        torneo.inscribirEquipo(equipo);
        guardar();
    }
    
    // --- MÉTODOS DE PERSISTENCIA ---

    public List<Partido> getPartidosSuetos() {
        return competicionServicio.getPartidosSuetos();
    }

    public void setPartidosSuetos(List<Partido> partidos) {
        competicionServicio.setPartidosSuetos(partidos);
    }
    
    // --- MÉTODOS DE VALIDACIÓN ---

    public boolean validarDisponibilidadCancha(Cancha cancha, LocalDateTime inicio, int duracionMinutos) {
        return competicionServicio.validarDisponibilidadCancha(cancha, inicio, duracionMinutos);
    }

    // --- MÉTODOS DE MODIFICACIÓN ---

    public boolean modificarJugador(String dni, String nuevoNombre, String nuevoApellido, String nuevaPosicion, int nuevoNivel) {
        boolean exito = jugadorServicio.modificar(dni, nuevoNombre, nuevoApellido, nuevaPosicion, nuevoNivel);
        if (exito) guardar();
        return exito;
    }
    
    public boolean modificarArbitro(String dni, String nuevoNombre, String nuevoApellido, String nuevaLicencia) {
        boolean exito = arbitroServicio.modificar(dni, nuevoNombre, nuevoApellido, nuevaLicencia); 
        if (exito) guardar();
        return exito;
    }

    public void editarEquipo(String nombreActual, String nuevoNombre, List<Jugador> nuevosJugadores) throws EquipoYaExisteException {
        equipoServicio.editar(nombreActual, nuevoNombre, nuevosJugadores);
        guardar();
    }

    public boolean modificarSede(String nombreActual, String nuevoNombre, String nuevaDireccion) {
        boolean exito = competicionServicio.modificarSede(nombreActual, nuevoNombre, nuevaDireccion);
        if (exito) guardar();
        return exito;
    }

    public boolean modificarCancha(String nombreSede, int numeroCancha, String nuevaSuperficie, boolean nuevaIluminacion) {
        // FIX: Corregido el nombre de la variable de 'competicionServicion' a 'competicionServicio'
        boolean exito = competicionServicio.modificarCancha(nombreSede, numeroCancha, nuevaSuperficie, nuevaIluminacion); 
        if (exito) guardar();
        return exito;
    }
    
    // --- MÉTODOS DE LÓGICA DE NEGOCIO ---

    public void finalizarPartido(Partido partido, String resultado, Equipo ganador) {
        if (partido == null || partido.isFinalizado()) return;
        
        partido.setResultado(resultado);
        partido.setFinalizado(true);
        Equipo perdedor = (ganador.equals(partido.getEquipoLocal())) ? partido.getEquipoVisitante() : partido.getEquipoLocal();
        ganador.incrementarPartidosGanados();
        ganador.getJugadores().forEach(Jugador::incrementarPartidosGanados);
        perdedor.incrementarPartidosPerdidos();
        perdedor.getJugadores().forEach(Jugador::incrementarPartidosPerdidos);

        guardar();
    }
    
    public void finalizarTorneo(Torneo torneo, Equipo ganador) {
        if (torneo == null || ganador == null) return;
        
        torneo.finalizarTorneo(ganador);
        ganador.incrementarTorneosGanados();
        ganador.getJugadores().forEach(Jugador::incrementarTorneosGanados);
        
        guardar();
    }
    
    // --- MÉTODOS DE ELIMINACIÓN ---
    
    public boolean eliminarJugador(String dni) {
        boolean eliminado = jugadorServicio.eliminar(dni);
        if (eliminado) guardar();
        return eliminado;
    }
    
    public boolean eliminarArbitro(String dni) {
        boolean eliminado = arbitroServicio.eliminar(dni);
        if (eliminado) guardar();
        return eliminado;
    }

    public boolean eliminarEquipo(String nombre) {
        Equipo equipo = buscarEquipoPorNombre(nombre);
        if (equipo == null) return false;

        for (Torneo torneo : competicionServicio.getTodosLosTorneos()) {
            torneo.getEquiposInscritos().remove(equipo);
            torneo.getPartidos().removeIf(p -> !p.isFinalizado() && (p.getEquipoLocal().equals(equipo) || p.getEquipoVisitante().equals(equipo)));
        }
        
        boolean eliminado = equipoServicio.eliminar(nombre);
        if (eliminado) guardar();
        return eliminado;
    }
    
    public boolean eliminarTorneo(Torneo torneo) {
        boolean eliminado = competicionServicio.eliminarTorneo(torneo);
        if (eliminado) guardar();
        return eliminado;
    }
    
    public boolean eliminarPartido(Partido partido) {
        boolean eliminado = competicionServicio.eliminarPartido(partido);
        if (eliminado) guardar();
        return eliminado;
    }
    
    // --- GETTERS Y MÉTODOS DE BÚSQUEDA ---
    
    public List<Sede> getSedes() { return competicionServicio.getSedes(); }
    public List<Jugador> getJugadoresRegistrados() { return jugadorServicio.getTodos(); }
    public List<Arbitro> getArbitrosRegistrados() { return arbitroServicio.getTodos(); }
    
    public Arbitro buscarArbitroPorDni(String dni) { return arbitroServicio.buscarPorDni(dni); }
    public List<Equipo> getEquiposRegistrados() { return equipoServicio.getTodos(); }
    public List<Partido> getPartidosPendientes() { return competicionServicio.getPartidosPendientes(); }
    public List<Torneo> getTodosLosTorneos() { return competicionServicio.getTodosLosTorneos(); }

    public Sede buscarSedePorNombre(String nombre) { return competicionServicio.buscarSedePorNombre(nombre); }
    public Jugador buscarJugadorPorDni(String dni) { return jugadorServicio.buscarPorDni(dni); }
    public Equipo buscarEquipoPorNombre(String nombre) { return equipoServicio.buscarPorNombre(nombre); }
    public Torneo buscarTorneoPorNombre(String nombre) { return competicionServicio.buscarTorneoPorNombre(nombre); }
    public List<Partido> getPartidosPorSedeYFecha(String nombreSede, LocalDate fecha) { return competicionServicio.getPartidosPorSedeYFecha(nombreSede, fecha); }
}