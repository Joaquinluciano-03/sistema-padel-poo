package modelo;

import java.io.Serializable;

/**
 * Representa una cancha de pádel dentro de una Sede.
 * Esta clase contiene información sobre las características físicas de la cancha.
 * Es serializable para permitir la persistencia de su estado.
 */
public class Cancha implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // Atributos de la clase
    private int id;
    private int numero;
    private String tipoSuperficie; // Ej: "Cesped Sintético", "Cemento"
    private boolean iluminacion;

    public Cancha(int numero, String tipoSuperficie, boolean iluminacion) {
        this.id = -1; // -1 indica que no está persistido en una base de datos.
        this.numero = numero;
        this.tipoSuperficie = tipoSuperficie;
        this.iluminacion = iluminacion;
    }

    // --- Getters y Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getTipoSuperficie() {
        return tipoSuperficie;
    }

    public void setTipoSuperficie(String tipoSuperficie) {
        this.tipoSuperficie = tipoSuperficie;
    }

    public boolean tieneIluminacion() {
        return iluminacion;
    }

    public void setIluminacion(boolean iluminacion) {
        this.iluminacion = iluminacion;
    }

    @Override
    public String toString() {
        return "Cancha N°" + numero + " (" + tipoSuperficie + "), Iluminación: " + (iluminacion ? "Sí" : "No");
    }
}