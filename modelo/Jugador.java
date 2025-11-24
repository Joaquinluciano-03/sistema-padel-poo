package modelo;

import java.io.Serializable;

/**
 * Representa a un jugador de pádel en el sistema.
 * Hereda de Persona y añade atributos específicos como estadísticas y nivel.
 * Es serializable para permitir la persistencia de su estado.
 */
public class Jugador extends Persona implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private int id;
    private String posicion;
    private int nivel;
    private int partidosGanados;
    private int partidosPerdidos;
    private int torneosGanados;

    public Jugador(String nombre, String apellido, String dni, String posicion, int nivel) {
        super(nombre, apellido, dni);
        this.id = -1;
        this.posicion = posicion;
        this.nivel = nivel;
        this.partidosGanados = 0;
        this.partidosPerdidos = 0;
        this.torneosGanados = 0;
    }

    // Métodos para actualizar estadísticas
    public void incrementarPartidosGanados() { this.partidosGanados++; }
    public void incrementarPartidosPerdidos() { this.partidosPerdidos++; }
    public void incrementarTorneosGanados() { this.torneosGanados++; }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPosicion() { return posicion; }
    public void setPosicion(String posicion) { this.posicion = posicion; }
    public int getNivel() { return nivel; }
    public void setNivel(int nivel) { this.nivel = nivel; }
    public int getPartidosGanados() { return partidosGanados; }
    public void setPartidosGanados(int partidosGanados) { this.partidosGanados = partidosGanados; }
    public int getPartidosPerdidos() { return partidosPerdidos; }
    public void setPartidosPerdidos(int partidosPerdidos) { this.partidosPerdidos = partidosPerdidos; }
    public int getTorneosGanados() { return torneosGanados; }
    public void setTorneosGanados(int torneosGanados) { this.torneosGanados = torneosGanados; }

    @Override
    public void presentarse() {
        System.out.println("Hola, soy el jugador " + getNombre() + " " + getApellido() +
                        ", mi posición es " + posicion + " y mi nivel es " + nivel + ".");
    }

    @Override
    public String toString() {
        return "Jugador: " + super.toString() + ", Posición: " + posicion + ", Nivel: " + nivel +
            ", Stats(G/P): " + partidosGanados + "/" + partidosPerdidos;
    }
}