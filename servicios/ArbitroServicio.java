package servicios;

import excepciones.JugadorYaExisteException;
import java.util.ArrayList;
import java.util.List;
import modelo.Arbitro;

/**
 * Clase de servicio responsable únicamente de la gestión de la colección de Árbitros.
 * Implementa IArbitroServicio
 */
public class ArbitroServicio implements IArbitroServicio {
    
    private List<Arbitro> arbitrosRegistrados = new ArrayList<>();

    /**
     * Registra un nuevo árbitro
     */
    @Override
    public void registrar(Arbitro arbitro) throws JugadorYaExisteException {
        boolean yaExiste = arbitrosRegistrados.stream()
                .anyMatch(a -> a.getDni().equalsIgnoreCase(arbitro.getDni()));

        if (yaExiste) {
            throw new JugadorYaExisteException(
                "Ya existe un árbitro con el DNI: " + arbitro.getDni(),
                arbitro.getDni()
            );
        }
        arbitrosRegistrados.add(arbitro);
    }

    @Override
    public Arbitro buscarPorDni(String dni) {
        return arbitrosRegistrados.stream().filter(a -> a.getDni().equalsIgnoreCase(dni)).findFirst().orElse(null);
    }
    
    @Override
    public boolean modificar(String dni, String nuevoNombre, String nuevoApellido, String nuevaLicencia) {
        Arbitro arbitro = buscarPorDni(dni);
        if (arbitro == null) {
            return false;
        }

        arbitro.setNombre(nuevoNombre);
        arbitro.setApellido(nuevoApellido);
        arbitro.setLicencia(nuevaLicencia);
        return true;
    }
    
    @Override
    public boolean eliminar(String dni) {
        return arbitrosRegistrados.removeIf(a -> a.getDni().equalsIgnoreCase(dni));
    }

    @Override
    public List<Arbitro> getTodos() {
        return arbitrosRegistrados;
    }
}