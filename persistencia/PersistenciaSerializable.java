package persistencia;

import controlador.GestorSistema;
import excepciones.EquipoYaExisteException;
import excepciones.JugadorYaExisteException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import modelo.Arbitro;
import modelo.Equipo;
import modelo.Jugador;
import modelo.Partido;
import modelo.Sede;
import modelo.Torneo;

/**
 * Clase que maneja el almacenamiento y la recuperación del estado del sistema 
 * utilizando serialización binaria en un archivo.
 * Implementa la interfaz IPersistencia para cumplir el Principio de Inversión de Dependencias (DIP).
 */
public class PersistenciaSerializable implements IPersistencia {

    private static final String FILE_PATH = "datos_padel.dat";

    @SuppressWarnings("unchecked") 
    @Override
    public void guardarDatos(GestorSistema gestor) {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            
            // Escribir colecciones globales
            oos.writeObject(gestor.getSedes());
            oos.writeObject(gestor.getJugadoresRegistrados());
            oos.writeObject(gestor.getArbitrosRegistrados());
            oos.writeObject(gestor.getEquiposRegistrados());
            oos.writeObject(gestor.getTodosLosTorneos());

            // --- NUEVO: Escribir la lista de Partidos Sueltos ---
            oos.writeObject(gestor.getPartidosSuetos()); 

            System.out.println("-> DATOS GUARDADOS AUTOMÁTICAMENTE en " + FILE_PATH);

        } catch (IOException e) {
            System.err.println("!! ERROR CRÍTICO AL GUARDAR LOS DATOS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void cargarDatos(GestorSistema gestor) {
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            
            // Leer datos en el mismo orden en que fueron escritos
            List<Sede> sedes = (List<Sede>) ois.readObject();
            List<Jugador> jugadores = (List<Jugador>) ois.readObject();
            List<Arbitro> arbitros = (List<Arbitro>) ois.readObject();
            List<Equipo> equipos = (List<Equipo>) ois.readObject();
            List<Torneo> torneos = (List<Torneo>) ois.readObject();
            
            // --- NUEVO: Leer la lista de Partidos Sueltos ---
            List<Partido> partidosSuetos = (List<Partido>) ois.readObject();


            // Limpiar listas actuales del sistema antes de cargar
            gestor.getSedes().clear();
            gestor.getJugadoresRegistrados().clear(); 
            gestor.getArbitrosRegistrados().clear();
            gestor.getEquiposRegistrados().clear();
            gestor.getTodosLosTorneos().clear(); 


            // 2. Cargar datos en el GestorSistema (Fachada)

            sedes.forEach(gestor::agregarSede); 

            // Cargar Arbitros
            for (Arbitro a : arbitros) {
                try {
                    gestor.registrarArbitro(a);
                } catch (JugadorYaExisteException e) {
                    System.err.println("Error de integridad de datos al cargar (Árbitro): " + e.getMessage());
                }
            }

            // Cargar Jugadores
            for (Jugador j : jugadores) {
                try {
                    gestor.registrarJugador(j);
                } catch (JugadorYaExisteException e) {
                    System.err.println("Error de integridad de datos al cargar (Jugador): " + e.getMessage());
                }
            }

            // Cargar Equipos
            for (Equipo eq : equipos) {
                try {
                    gestor.registrarEquipo(eq);
                } catch (EquipoYaExisteException ex) { 
                    System.err.println("Error de integridad de datos al cargar (Equipo): " + ex.getMessage());
                }
            }

            // Cargar Torneos globalmente
            torneos.forEach(gestor::registrarTorneo);
            
            // --- NUEVO: Cargar Partidos Sueltos ---
            // Asumimos que los partidos sueltos deben cargarse directamente en el servicio.
            // Puesto que CompeticionServicio ya tiene el método setPartidosSuetos(List<Partido> partidos), lo delegamos.
            gestor.setPartidosSuetos(partidosSuetos);


            System.out.println("Datos cargados correctamente desde " + FILE_PATH);

        } catch (FileNotFoundException e) {
            System.out.println("No se encontró el archivo de datos. Se iniciará un sistema nuevo.");
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar los datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}