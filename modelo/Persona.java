package modelo;

import java.io.Serializable;

/**
 * Clase abstracta que sirve como base para todas las entidades "persona" del sistema.
 * Define los atributos y comportamientos comunes (nombre, apellido, dni).
 * Es serializable para que sus clases hijas también puedan serlo.
 */
public abstract class Persona implements Serializable {
    
    // Identificador de versión para la serialización.
    private static final long serialVersionUID = 1L;

    // Atributos comunes a todas las personas
    private String nombre;
    private String apellido;
    private String dni;

    public Persona(String nombre, String apellido, String dni) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    /**
     * Método abstracto que obliga a las clases hijas a implementar su propia
     * forma de presentarse.
     */
    public abstract void presentarse();

    @Override
    public String toString() {
        return nombre + " " + apellido + " (DNI: " + dni + ")";
    }
}