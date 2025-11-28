package servicios;

import excepciones.JugadorYaExisteException;
import java.util.ArrayList;
import java.util.List;
import modelo.Jugador;

/**
 * Clase de servicio responsable únicamente de la gestión de la colección de Jugadores.
 * Implementa IJugadorServicio para cumplir el DIP y SRP/ISP.
 */
public class JugadorServicio implements IJugadorServicio { // Implementa la interfaz IJugadorServicio
    private List<Jugador> jugadoresRegistrados = new ArrayList<>();

    @Override
    public void registrar(Jugador jugador) throws JugadorYaExisteException {
        // Comprobamos si ya existe un jugador con el mismo DNI letra y num
        boolean yaExiste = jugadoresRegistrados.stream()
                .anyMatch(j -> j.getDni().equalsIgnoreCase(jugador.getDni()));

        if (yaExiste) {
            // Si existe, lanzamos la nueva excepción con el DNI conflictivo
            throw new JugadorYaExisteException(
                "Ya existe un jugador con el DNI: " + jugador.getDni(),
                jugador.getDni()
            );
        }
        
        jugadoresRegistrados.add(jugador);
    }
    
    @Override
    public boolean modificar(String dni, String nuevoNombre, String nuevoApellido, String nuevaPosicion, int nuevoNivel) {
        Jugador jugador = buscarPorDni(dni);
        if (jugador == null) {
            return false;
        }

        jugador.setNombre(nuevoNombre);
        jugador.setApellido(nuevoApellido);
        jugador.setPosicion(nuevaPosicion);
        jugador.setNivel(nuevoNivel);
        return true;
    }

    @Override
    public boolean eliminar(String dni) {
        return jugadoresRegistrados.removeIf(j -> j.getDni().equalsIgnoreCase(dni));
    }

    @Override
    public Jugador buscarPorDni(String dni) {
        return jugadoresRegistrados.stream().filter(j -> j.getDni().equalsIgnoreCase(dni)).findFirst().orElse(null);
    }

    @Override
    public List<Jugador> getTodos() {
        return jugadoresRegistrados;
    }

}