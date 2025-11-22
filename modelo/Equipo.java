package modelo;

import java.io.Serializable;
import java.util.List;

/**
 * Representa a un equipo de pádel, compuesto por hasta dos jugadores.
 * El constructor ahora requiere la lista inicial de jugadores.
 */
public class Equipo implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private List<Jugador> jugadores;
    private int partidosGanados;
    private int partidosPerdidos;
    private int torneosGanados;

    // --- CONSTRUCTOR MODIFICADO: Ahora requiere los jugadores ---
    public Equipo(String nombre, List<Jugador> jugadores) {
        if (jugadores == null || jugadores.size() != 2) {
            throw new IllegalArgumentException("Un equipo debe crearse con exactamente 2 jugadores.");
        }
        this.id = -1; 
        this.nombre = nombre;
        this.jugadores = jugadores;
        this.partidosGanados = 0;
        this.partidosPerdidos = 0;
        this.torneosGanados = 0;
    }

    // Eliminamos agregarJugador() y quitarJugador() ya que la GUI
    // manejará la lista directamente y llamará a setJugadores.
    
    // --- Métodos para actualizar estadísticas ---

    public void incrementarPartidosGanados() {
        this.partidosGanados++;
    }

    public void incrementarPartidosPerdidos() {
        this.partidosPerdidos++;
    }

    public void incrementarTorneosGanados() {
        this.torneosGanados++;
    }
    
    // --- Getters y Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Jugador> getJugadores() { return jugadores; }
    
    // El setter se usa ahora para reemplazar la lista completa de jugadores.
    public void setJugadores(List<Jugador> jugadores) { 
        this.jugadores = jugadores; 
    }
    
    public int getPartidosGanados() { return partidosGanados; }
    public void setPartidosGanados(int partidosGanados) { this.partidosGanados = partidosGanados; }
    public int getPartidosPerdidos() { return partidosPerdidos; }
    public void setPartidosPerdidos(int partidosPerdidos) { this.partidosPerdidos = partidosPerdidos; }
    public int getTorneosGanados() { return torneosGanados; }
    public void setTorneosGanados(int torneosGanados) { this.torneosGanados = torneosGanados; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Equipo: ").append(nombre)
          .append(" (Victorias/Derrotas: ").append(partidosGanados)
          .append("/").append(partidosPerdidos).append(")\n");
          
        if (jugadores.isEmpty()) {
            sb.append("  (Sin jugadores asignados)\n");
        } else {
            for (Jugador j : jugadores) {
                sb.append("  - ").append(j.getNombre()).append(" ").append(j.getApellido()).append(" (DNI: ").append(j.getDni()).append(")\n");
            }
        }
        return sb.toString();
    }
}