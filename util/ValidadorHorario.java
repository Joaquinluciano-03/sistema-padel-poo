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

    // Horarios de operación
    private final LocalTime HORA_APERTURA = LocalTime.of(8, 0);
    private final LocalTime HORA_CIERRE = LocalTime.of(22, 0);
    
    // Eliminamos la constante de duración estándar ya que ahora usaremos la real del partido.

    /**
     * Verifica si una cancha está disponible en un rango de tiempo específico
     * dentro de una lista de partidos que ya están programados para esa fecha.
     * @param cancha La cancha a verificar.
     * @param inicio Fecha y hora de inicio deseada.
     * @param duracionMinutos Duración del partido propuesto.
     * @param partidosDelDia Lista de partidos ya programados para esa fecha y sede.
     * @return true si está disponible, false si hay conflicto.
     */
    public boolean validarDisponibilidadCancha(Cancha cancha, LocalDateTime inicio, int duracionMinutos, List<Partido> partidosDelDia) {
        LocalDateTime fin = inicio.plusMinutes(duracionMinutos);
        
        // 1. Validación de Horario de Apertura/Cierre
        if (inicio.toLocalTime().isBefore(HORA_APERTURA)) {
            return false;
        }
        // Si el partido termina después del cierre, no es válido.
        // Nota: Si termina exactamente a las 22:00, es válido (isAfter es estricto >).
        if (fin.toLocalTime().isAfter(HORA_CIERRE) && !fin.toLocalTime().equals(LocalTime.MIDNIGHT)) {
             // Manejo especial: si termina a las 00:00 del día siguiente, técnicamente se pasó de las 22:00 del día actual.
             // Pero con la lógica simple de LocalTime, 22:01 es after 22:00.
             return false;
        }

        for (Partido p : partidosDelDia) {
            
            // Filtramos solo partidos que están en la misma cancha
            // (Importante: Usar equals si está implementado, o comparar IDs/Números)
            if (p.getCancha().getNumero() != cancha.getNumero()) {
                continue; 
            }
            
            LocalDateTime pInicio = p.getFechaHora();
            
            // CORRECCIÓN CRÍTICA: Usar la duración real del partido guardado
            int duracionReal = p.getDuracionMinutos();
            
            // Si por alguna razón es 0 (datos antiguos), usamos un default de seguridad (ej. 90 min)
            if (duracionReal <= 0) duracionReal = 90; 
            
            LocalDateTime pFin = pInicio.plusMinutes(duracionReal);

            // Lógica de intersección de intervalos:
            // Dos intervalos [A, B] y [C, D] se solapan si (A < D) and (B > C)
            if (inicio.isBefore(pFin) && fin.isAfter(pInicio)) {
                return false; // Hay solapamiento
            }
        }
        return true;
    }
}