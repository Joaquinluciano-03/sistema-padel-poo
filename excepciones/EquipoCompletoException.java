package excepciones;

import java.io.Serializable;
import modelo.Equipo;
import modelo.Jugador;

/**
 * Excepción personalizada que se lanza cuando se intenta agregar un jugador a un equipo completo.
 */
public class EquipoCompletoException extends Exception implements Serializable {
    
    private static final long serialVersionUID = 2L; // Incrementamos la versión

    // Atributos adicionales para dar contexto al error
    private final Equipo equipo;
    private final Jugador jugadorRechazado;

    public EquipoCompletoException(String message, Equipo equipo, Jugador jugadorRechazado) {
        super(message);
        this.equipo = equipo;
        this.jugadorRechazado = jugadorRechazado;
    }

    // Getters para que el código que captura la excepción pueda acceder a esta información
    public Equipo getEquipo() {
        return equipo;
    }

    public Jugador getJugadorRechazado() {
        return jugadorRechazado;
    }
}