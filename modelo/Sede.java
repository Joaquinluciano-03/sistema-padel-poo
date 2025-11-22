package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una sede o club de pádel.
 * Una sede contiene canchas. La lista de torneos se ha movido al servicio de competición.
 */
public class Sede implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // Atributos de la clase
    private int id;
    private String nombre;
    private String direccion;
    
    // Relación de Composición: Una Sede "tiene" Canchas.
    private List<Cancha> canchas;
    // La lista de torneos se ha ELIMINADO de Sede.


    public Sede(String nombre, String direccion) {
        this.id = -1; // -1 indica que no está persistido en una base de datos.
        this.nombre = nombre;
        this.direccion = direccion;
        this.canchas = new ArrayList<>();
    }
    
    // --- Métodos para agregar elementos a las listas ---

    public void agregarCancha(Cancha cancha) {
        this.canchas.add(cancha);
    }
    
    // Los métodos agregarTorneo(), getTorneos() y setTorneos() han sido ELIMINADOS.

    // --- Getters y Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Cancha> getCanchas() {
        return canchas;
    }

    public void setCanchas(List<Cancha> canchas) {
        this.canchas = canchas;
    }

    @Override
    public String toString() {
        return "Sede: " + nombre + ", Dirección: " + direccion;
    }
}