package util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import modelo.Cancha;
import modelo.Partido;

/**
 * Clase responsable de la lógica compleja de validación de horarios y solapamiento
 * de partidos para una cancha específica. 
 * Cumple con el Principio de Responsabilidad Única (SRP).
 */
public class ValidadorHorario {

    // Horarios de operación (constantes movidas del panel)
    private final LocalTime HORA_APERTURA = LocalTime.of(8, 0);
    private final LocalTime HORA_CIERRE = LocalTime.of(22, 0);
    
    // Asumimos una duración estándar de 90 min para los partidos existentes si no se guarda duración
    private final int DURACION_ESTANDAR_MINUTOS = 90; 

    /**
     * Verifica si una cancha está disponible en un rango de tiempo específico
     * dentro de una lista de partidos que ya están programados para esa fecha.
     * * @param cancha La cancha a verificar.
     * @param inicio Fecha y hora de inicio deseada.
     * @param duracionMinutos Duración del partido propuesto.
     * @param partidosDelDia Lista de partidos ya programados para esa fecha y sede.
     * @return true si está disponible, false si hay conflicto.
     */
    public boolean validarDisponibilidadCancha(Cancha cancha, LocalDateTime inicio, int duracionMinutos, List<Partido> partidosDelDia) {
        LocalDateTime fin = inicio.plusMinutes(duracionMinutos);
        
        // 1. Validación de Horario de Apertura/Cierre (Regla de Negocio)
        if (inicio.toLocalTime().isBefore(HORA_APERTURA) || fin.toLocalTime().isAfter(HORA_CIERRE)) {
            return false;
        }

        for (Partido p : partidosDelDia) {
            
            // Filtramos solo partidos que están en la misma cancha (la lista ya está filtrada por día y sede)
            if (p.getCancha().getNumero() != cancha.getNumero()) {
                continue; 
            }
            
            LocalDateTime pInicio = p.getFechaHora();
            // Usamos la duración estándar para los partidos que no tienen duración explícita
            LocalDateTime pFin = pInicio.plusMinutes(DURACION_ESTANDAR_MINUTOS); 

            // Lógica de intersección de intervalos:
            // Dos intervalos [A, B] y [C, D] se solapan si (A < D) and (B > C)
            if (inicio.isBefore(pFin) && fin.isAfter(pInicio)) {
                return false; // Hay solapamiento
            }
        }
        return true;
    }
}