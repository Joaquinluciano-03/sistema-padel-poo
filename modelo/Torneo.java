package modelo;

import excepciones.InscripcionException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una competición de pádel que agrupa múltiples equipos y partidos.
 * Gestiona su propio ciclo de vida a través de un estado (pendiente, en curso, finalizado).
 * Es serializable para permitir la persistencia de su estado.
 */
public class Torneo implements Serializable {
    
    private static final long serialVersionUID = 1L;

    public enum EstadoTorneo { PENDIENTE, EN_CURSO, FINALIZADO }

    // Atributos de la clase
    private int id;
    private String nombre;
    private List<Equipo> equiposInscritos;
    private List<Partido> partidos;
    private final int maxEquipos;
    private EstadoTorneo estado;
    private Equipo ganador;


    public Torneo(String nombre, int maxEquipos) {
        this.id = -1;
        this.nombre = nombre;
        this.maxEquipos = maxEquipos;
        this.equiposInscritos = new ArrayList<>();
        this.partidos = new ArrayList<>();
        this.estado = EstadoTorneo.PENDIENTE;
        this.ganador = null;
    }
    
    // --- Métodos de Lógica de Negocio ---

    public void inscribirEquipo(Equipo equipo) throws InscripcionException {
        if (equiposInscritos.size() >= maxEquipos) {
            // Se lanza la excepción mejorada con el motivo TORNEO_LLENO.
            throw new InscripcionException(
                "El torneo '" + nombre + "' está lleno.",
                InscripcionException.Motivo.TORNEO_LLENO,
                this,
                equipo
            );
        }
        if (equiposInscritos.contains(equipo)) {
            // Se lanza la excepción mejorada con el motivo EQUIPO_YA_INSCRITO.
            throw new InscripcionException(
                "El equipo '" + equipo.getNombre() + "' ya está inscrito en este torneo.",
                InscripcionException.Motivo.EQUIPO_YA_INSCRITO,
                this,
                equipo
            );
        }
        equiposInscritos.add(equipo);
    }
    
    public void agregarPartido(Partido partido) {
        this.partidos.add(partido);
    }

    public void finalizarTorneo(Equipo ganador) {
        this.ganador = ganador;
        this.estado = EstadoTorneo.FINALIZADO;
    }

    // --- Getters y Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Equipo> getEquiposInscritos() { return equiposInscritos; }
    public void setEquiposInscritos(List<Equipo> equiposInscritos) { this.equiposInscritos = equiposInscritos; }
    public List<Partido> getPartidos() { return partidos; }
    public void setPartidos(List<Partido> partidos) { this.partidos = partidos; }
    public int getMaxEquipos() { return maxEquipos; }
    public EstadoTorneo getEstado() { return estado; }
    public void setEstado(EstadoTorneo estado) { this.estado = estado; }
    public Equipo getGanador() { return ganador; }
    public void setGanador(Equipo ganador) { this.ganador = ganador; }

    @Override
    public String toString() {
        return "Torneo: " + nombre + " (" + equiposInscritos.size() + "/" + maxEquipos + " equipos) [" + estado + "]";
    }
}