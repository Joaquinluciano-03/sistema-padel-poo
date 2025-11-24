package controlador;

import excepciones.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import modelo.*;
import persistencia.IPersistencia;
import servicios.*;

public class GestorSistema {
    
    private IPersistencia persistencia; 
    
    // Interfaces de servicio
    private IJugadorServicio jugadorServicio; 
    private IArbitroServicio arbitroServicio; 
    private IEquipoServicio equipoServicio; 
    private ICompeticionServicio competicionServicio; 

    public GestorSistema() {
        // Inicialización de implementaciones
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
    
    // --- MÉTODOS DE REGISTRO ---
    
    public void agregarSede(Sede sede) throws IllegalArgumentException {
        competicionServicio.agregarSede(sede);
        guardar();
    }

    public void registrarTorneo(Torneo torneo) {
        competicionServicio.registrarTorneo(torneo);
        guardar();
    }
    
    public void registrarJugador(Jugador jugador) throws JugadorYaExisteException {
        // Validación cruzada: DNI no debe ser de árbitro
        if (arbitroServicio.buscarPorDni(jugador.getDni()) != null) {
             throw new JugadorYaExisteException("El DNI ya está registrado como árbitro.", jugador.getDni());
        }
        jugadorServicio.registrar(jugador);
        guardar();
    }
    
    public void registrarArbitro(Arbitro arbitro) throws JugadorYaExisteException {
        // Validación cruzada: DNI no debe ser de jugador
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
    
    // --- MÉTODOS DE PERSISTENCIA (Partidos Sueltos) ---
    public List<Partido> getPartidosSuetos() {
        return competicionServicio.getPartidosSuetos();
    }

    public void setPartidosSuetos(List<Partido> partidos) {
        competicionServicio.setPartidosSuetos(partidos);
    }
    
    // --- VALIDACIÓN ---
    public boolean validarDisponibilidadCancha(Cancha cancha, LocalDateTime inicio, int duracionMinutos) {
        return competicionServicio.validarDisponibilidadCancha(cancha, inicio, duracionMinutos);
    }

    // --- MODIFICACIÓN ---
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
        try {
            equipoServicio.editar(nombreActual, nuevoNombre, nuevosJugadores);
            guardar();
        } catch (IllegalArgumentException e) {
            throw e; 
        }
    }

    public boolean modificarSede(String nombreActual, String nuevoNombre, String nuevaDireccion) {
        boolean exito = competicionServicio.modificarSede(nombreActual, nuevoNombre, nuevaDireccion);
        if (exito) guardar();
        return exito;
    }

    public boolean modificarCancha(String nombreSede, int numeroCancha, String nuevaSuperficie, boolean nuevaIluminacion) {
        boolean exito = competicionServicio.modificarCancha(nombreSede, numeroCancha, nuevaSuperficie, nuevaIluminacion); 
        if (exito) guardar();
        return exito;
    }
    
    // --- FINALIZACIÓN ---
    public void finalizarPartido(Partido partido, String resultado, Equipo ganador) {
        if (partido == null || partido.isFinalizado()) return;
        
        partido.setResultado(resultado);
        partido.setFinalizado(true);
        
        // Actualizar estadísticas
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
    
    // --- ELIMINACIÓN ---
    public boolean eliminarJugador(String dni) {
        // Primero eliminar de equipos para integridad
        Jugador j = jugadorServicio.buscarPorDni(dni);
        if (j != null) {
             for (Equipo e : equipoServicio.getTodos()) {
                 e.getJugadores().remove(j);
             }
        }
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
        Equipo equipo = equipoServicio.buscarPorNombre(nombre);
        if (equipo == null) return false;

        // Eliminar de torneos y partidos pendientes
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
    
    public boolean eliminarSede(String nombre) {
        boolean eliminado = competicionServicio.eliminarSede(nombre);
        if (eliminado) guardar();
        return eliminado;
    }

    public boolean eliminarCancha(Sede sede, int numeroCancha) {
        boolean eliminado = competicionServicio.eliminarCancha(sede, numeroCancha);
        if (eliminado) guardar();
        return eliminado;
    }
    
    // --- GETTERS DELEGADOS ---
    public List<Sede> getSedes() { return competicionServicio.getSedes(); }
    public List<Jugador> getJugadoresRegistrados() { return jugadorServicio.getTodos(); }
    
    // FIX: Ahora usamos el servicio correcto
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