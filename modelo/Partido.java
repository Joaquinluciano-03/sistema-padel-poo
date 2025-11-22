package modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa un único encuentro de pádel entre dos equipos.
 * Se le añade una referencia opcional al torneo al que pertenece.
 */
public class Partido implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // Atributos de la clase
    private int id;
    private Equipo equipoLocal;
    private Equipo equipoVisitante;
    private String resultado;
    private LocalDateTime fechaHora;
    private Cancha cancha;
    private Arbitro arbitro;
    private boolean finalizado;
    private Torneo torneoPerteneciente; // <-- NUEVO ATRIBUTO

    /**
     * Constructor modificado para incluir el torneo (puede ser null).
     */
    public Partido(Equipo equipoLocal, Equipo equipoVisitante, LocalDateTime fechaHora, Cancha cancha, Arbitro arbitro, Torneo torneoPerteneciente) {
        this.id = -1; 
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.fechaHora = fechaHora;
        this.cancha = cancha;
        this.arbitro = arbitro;
        this.resultado = "Pendiente";
        this.finalizado = false;
        this.torneoPerteneciente = torneoPerteneciente; // <-- ASIGNACIÓN
    }
    
    // Sobrecarga del constructor existente para partidos sueltos (torneoPerteneciente = null)
    public Partido(Equipo equipoLocal, Equipo equipoVisitante, LocalDateTime fechaHora, Cancha cancha, Arbitro arbitro) {
        this(equipoLocal, equipoVisitante, fechaHora, cancha, arbitro, null);
    }


    /**
     * Devuelve una descripción simple del enfrentamiento.
     * @return Una cadena con el formato "NombreEquipoLocal vs NombreEquipoVisitante".
     */
    public String getDescripcion() {
        String base = equipoLocal.getNombre() + " vs " + equipoVisitante.getNombre();
        if (torneoPerteneciente != null) {
            base += " (Torneo: " + torneoPerteneciente.getNombre() + ")";
        }
        return base;
    }

    // --- Getters y Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Equipo getEquipoLocal() {
        return equipoLocal;
    }

    public void setEquipoLocal(Equipo equipoLocal) {
        this.equipoLocal = equipoLocal;
    }

    public Equipo getEquipoVisitante() {
        return equipoVisitante;
    }

    public void setEquipoVisitante(Equipo equipoVisitante) {
        this.equipoVisitante = equipoVisitante;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Cancha getCancha() {
        return cancha;
    }

    public void setCancha(Cancha cancha) {
        this.cancha = cancha;
    }

    public Arbitro getArbitro() {
        return arbitro;
    }

    public void setArbitro(Arbitro arbitro) {
        this.arbitro = arbitro;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public void setFinalizado(boolean finalizado) {
        this.finalizado = finalizado;
    }
    
    public Torneo getTorneoPerteneciente() { // <-- NUEVO GETTER
        return torneoPerteneciente;
    }

    public void setTorneoPerteneciente(Torneo torneoPerteneciente) { // <-- NUEVO SETTER
        this.torneoPerteneciente = torneoPerteneciente;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return getDescripcion() + 
               " | Fecha: " + fechaHora.format(formatter) +
               " | Resultado: [" + resultado + "]";
    }
}