package util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import modelo.Equipo;
import modelo.Jugador;

/**
 * Clase de utilidad que proporciona métodos estáticos para generar rankings.
 * No está diseñada para ser instanciada; todos sus métodos se acceden de forma estática 
 */
public class Ranking {

    /**
     * Constructor privado para prevenir la instanciación de esta clase de utilidad.
     */
    private Ranking() {}

    public static void ordenarJugadoresPorRatio(List<Jugador> jugadores) {
        // Collections.sort() ordena la lista. Le pasamos un Comparator personalizado
        // para definir nuestra propia lógica de ordenación.
        Collections.sort(jugadores, new Comparator<Jugador>() {
            @Override
            public int compare(Jugador j1, Jugador j2) {
                double ratio1 = calcularRatio(j1.getPartidosGanados(), j1.getPartidosPerdidos());
                double ratio2 = calcularRatio(j2.getPartidosGanados(), j2.getPartidosPerdidos());
                return Double.compare(ratio2, ratio1);
            }
        });
    }

    public static void ordenarEquiposPorRatio(List<Equipo> equipos) {
        Collections.sort(equipos, new Comparator<Equipo>() {
            @Override
            public int compare(Equipo e1, Equipo e2) {
                double ratio1 = calcularRatio(e1.getPartidosGanados(), e1.getPartidosPerdidos());
                double ratio2 = calcularRatio(e2.getPartidosGanados(), e2.getPartidosPerdidos());
                
                // Orden descendente
                return Double.compare(ratio2, ratio1);
            }
        });
    }

    private static double calcularRatio(int ganados, int perdidos) {
        int total = ganados + perdidos;
        
        // Prevenir la división por cero
        if (total == 0) {
            return 0.0;
        }
        
        // Hacemos un casting a double para asegurar una división con decimales.
        // Si no, la división de enteros daría 0 en la mayoría de los casos.
        return (double) ganados / total;
    }
}