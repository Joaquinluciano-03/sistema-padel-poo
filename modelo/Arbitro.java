package modelo;

import java.io.Serializable;

/**
 * Representa a un árbitro en el sistema de gestión de pádel.
 * Esta clase hereda las propiedades básicas de una Persona y añade
 * atributos específicos para un árbitro, como su número de licencia.
 * Es serializable para permitir la persistencia de su estado.
 */
public class Arbitro extends Persona implements Serializable {
    
    // Identificador de versión para la serialización.
    private static final long serialVersionUID = 1L;

    // Atributo específico de la clase Arbitro
    private String licencia;

    public Arbitro(String nombre, String apellido, String dni, String licencia) {
        super(nombre, apellido, dni);
        this.licencia = licencia;
    }

    // --- Getters y Setters ---

    public String getLicencia() {
        return licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }

    /**
     * Implementación del método abstracto 'presentarse' heredado de Persona.
     * Muestra por consola un mensaje de presentación específico para un árbitro.
     */
    @Override
    public void presentarse() {
        // Utilizamos los getters de la clase Persona para acceder a nombre y apellido.
        System.out.println("Soy " + getNombre() + " " + getApellido() + ", árbitro con licencia " + licencia + ".");
    }
    
    /**
     * Genera una representación en formato de texto del objeto Arbitro.
     * Utilizado para mostrar la información en la interfaz gráfica o en logs.
     * @return Una cadena con los detalles completos del árbitro.
     */
    @Override
    public String toString() {
        // Reutilizamos el método toString() de la clase padre y le añadimos la información específica.
        return "Árbitro: " + super.toString() + ", Licencia: " + licencia;
    }
}