package servicios;

import excepciones.EquipoYaExisteException;
import java.util.ArrayList;
import java.util.List;
import modelo.Equipo;
import modelo.Jugador;

/**
 * Clase de servicio responsable de la gestión de la colección de equipos.
 * Implementa IEquipoServicio para cumplir el DIP.
 */
public class EquipoServicio implements IEquipoServicio { // <-- Implementa la interfaz
    private List<Equipo> equiposRegistrados = new ArrayList<>();

    @Override
    public void registrar(Equipo equipo) throws EquipoYaExisteException {
        // Comprobamos si ya existe un equipo con el mismo nombre (ignorando mayúsculas/minúsculas)
        boolean yaExiste = equiposRegistrados.stream()
                .anyMatch(e -> e.getNombre().equalsIgnoreCase(equipo.getNombre()));

        if (yaExiste) {
            // Si existe, lanzamos la nueva excepción con el nombre conflictivo
            throw new EquipoYaExisteException(
                "Ya existe un equipo con el nombre: " + equipo.getNombre(),
                equipo.getNombre()
            );
        }
        
        equiposRegistrados.add(equipo);
    }
    
    /**
     * NUEVO MÉTODO CENTRALIZADO: Edita el nombre y la composición de jugadores de un equipo.
     */
    @Override
    public void editar(String nombreActual, String nuevoNombre, List<Jugador> nuevosJugadores) throws EquipoYaExisteException, IllegalArgumentException {
        Equipo equipo = buscarPorNombre(nombreActual);
        if (equipo == null) return; 

        // 1. Validar la nueva lista de jugadores
        if (nuevosJugadores == null || nuevosJugadores.size() != 2) {
            throw new IllegalArgumentException("Un equipo debe tener exactamente 2 jugadores.");
        }

        // 2. Validar el nuevo nombre (si cambió)
        if (!nombreActual.equalsIgnoreCase(nuevoNombre)) {
            Equipo otro = buscarPorNombre(nuevoNombre);
            if (otro != null) {
                throw new EquipoYaExisteException("Ya existe un equipo con el nombre " + nuevoNombre, nuevoNombre);
            }
        }
        
        // 3. Aplicar cambios
        equipo.setNombre(nuevoNombre);
        equipo.setJugadores(nuevosJugadores);
    }

    @Override
    public boolean eliminar(String nombre) {
        return equiposRegistrados.removeIf(e -> e.getNombre().equalsIgnoreCase(nombre));
    }

    @Override
    public Equipo buscarPorNombre(String nombre) {
        return equiposRegistrados.stream().filter(e -> e.getNombre().equalsIgnoreCase(nombre)).findFirst().orElse(null);
    }

    @Override
    public List<Equipo> getTodos() {
        return equiposRegistrados;
    }
}