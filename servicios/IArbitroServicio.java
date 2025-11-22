package servicios;

import excepciones.JugadorYaExisteException;
import java.util.List;
import modelo.Arbitro;

/**
 * Interfaz que define el contrato exclusivo para los servicios de gestión de Árbitros.
 * Cumple con el Principio de Segregación de Interfaces (ISP).
 */
public interface IArbitroServicio {
    
    void registrar(Arbitro arbitro) throws JugadorYaExisteException;
    
    Arbitro buscarPorDni(String dni);
    
    boolean modificar(String dni, String nuevoNombre, String nuevoApellido, String nuevaLicencia);
    
    boolean eliminar(String dni);
    
    List<Arbitro> getTodos();
}