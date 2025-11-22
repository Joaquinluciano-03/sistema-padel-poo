package excepciones;

import java.io.Serializable;

/**
 * Excepción que se lanza al intentar registrar un jugador con un DNI
 * que ya existe en el sistema.
 */
public class JugadorYaExisteException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String dniDuplicado;

    public JugadorYaExisteException(String message, String dniDuplicado) {
        super(message);
        this.dniDuplicado = dniDuplicado;
    }

    public String getDniDuplicado() {
        return dniDuplicado;
    }
}