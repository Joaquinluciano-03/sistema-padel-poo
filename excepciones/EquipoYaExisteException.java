package excepciones;

import java.io.Serializable;

/**
 * Excepción que se lanza al intentar crear un equipo con un nombre que ya existe en el sistema.
 */
public class EquipoYaExisteException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String nombreDuplicado;

    public EquipoYaExisteException(String message, String nombreDuplicado) {
        super(message);
        this.nombreDuplicado = nombreDuplicado;
    }

    public String getNombreDuplicado() {
        return nombreDuplicado;
    }
}