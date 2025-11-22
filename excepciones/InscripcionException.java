package excepciones;

import java.io.Serializable;
import modelo.Equipo;
import modelo.Torneo;

/**
 * Excepción para errores durante la inscripción a un torneo.
 */
public class InscripcionException extends Exception implements Serializable {

    private static final long serialVersionUID = 2L;

    // Enum interno para clasificar el tipo de error de inscripción
    public enum Motivo {
        TORNEO_LLENO,
        EQUIPO_YA_INSCRITO
    }

    private final Motivo motivo;
    private final Torneo torneo;
    private final Equipo equipo;

    public InscripcionException(String message, Motivo motivo, Torneo torneo, Equipo equipo) {
        super(message);
        this.motivo = motivo;
        this.torneo = torneo;
        this.equipo = equipo;
    }

    // Getters para que el código que captura la excepción pueda reaccionar
    public Motivo getMotivo() {
        return motivo;
    }

    public Torneo getTorneo() {
        return torneo;
    }

    public Equipo getEquipo() {
        return equipo;
    }
}