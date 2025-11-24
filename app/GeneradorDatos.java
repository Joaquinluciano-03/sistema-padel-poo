package app;

import java.time.LocalDateTime;
import java.util.Arrays;

import controlador.GestorSistema;
import modelo.Arbitro;
import modelo.Cancha;
import modelo.Equipo;
import modelo.Jugador;
import modelo.Partido;
import modelo.Sede;
import modelo.Torneo;
import persistencia.IPersistencia;
import persistencia.PersistenciaSerializable;

/**
 * Clase de utilidad para poblar el sistema con un set completo de datos de prueba.
 * Incluye: Sedes, Canchas, Árbitros, Jugadores, Equipos, 
 * Torneos (Finalizados y En Curso) y Partidos (Sueltos y de Torneo).
 */
public class GeneradorDatos {

    public static void main(String[] args) {
        System.out.println("=== INICIANDO GENERACIÓN DE DATOS DE PRUEBA COMPLETOS ===");

        // 1. Inicializar el sistema
        GestorSistema gestor = new GestorSistema();
        IPersistencia persistencia = new PersistenciaSerializable();
        gestor.setPersistencia(persistencia);

        try {
            // ---------------------------------------------------------
            // 2. CREAR SEDES Y CANCHAS
            // ---------------------------------------------------------
            System.out.println("-> Generando Sedes y Canchas...");
            
            Sede sedeCentro = new Sede("Padel Club Centro", "Av. Libertador 1000");
            Cancha c1 = new Cancha(1, "Cesped Sintético", true);
            Cancha c2 = new Cancha(2, "Cemento", false);
            sedeCentro.agregarCancha(c1);
            sedeCentro.agregarCancha(c2);
            gestor.agregarSede(sedeCentro);

            Sede sedeNorte = new Sede("Norte Padel", "Calle Los Alamos 500");
            Cancha c3 = new Cancha(1, "Cristal", true);
            Cancha c4 = new Cancha(2, "Sintético Premium", true);
            sedeNorte.agregarCancha(c3);
            sedeNorte.agregarCancha(c4);
            gestor.agregarSede(sedeNorte);

            // ---------------------------------------------------------
            // 3. CREAR ÁRBITROS
            // ---------------------------------------------------------
            System.out.println("-> Generando Árbitros...");
            
            Arbitro a1 = new Arbitro("Horacio", "Elizondo", "1001", "LIC-PRO-01");
            Arbitro a2 = new Arbitro("Nestor", "Pitana", "1002", "LIC-NAC-02");
            Arbitro a3 = new Arbitro("Pierluigi", "Collina", "1003", "LIC-INT-99");
            
            gestor.registrarArbitro(a1);
            gestor.registrarArbitro(a2);
            gestor.registrarArbitro(a3);

            // ---------------------------------------------------------
            // 4. CREAR JUGADORES (8)
            // ---------------------------------------------------------
            System.out.println("-> Generando Jugadores...");
            
            Jugador j1 = new Jugador("Alejandro", "Galan", "2001", "Revés", 9);
            Jugador j2 = new Jugador("Juan", "Lebron", "2002", "Drive", 9);
            
            Jugador j3 = new Jugador("Paquito", "Navarro", "2003", "Revés", 8);
            Jugador j4 = new Jugador("Martin", "Di Nenno", "2004", "Drive", 8);
            
            Jugador j5 = new Jugador("Fernando", "Belasteguin", "2005", "Revés", 10);
            Jugador j6 = new Jugador("Sanyo", "Gutierrez", "2006", "Drive", 9);
            
            Jugador j7 = new Jugador("Agustin", "Tapia", "2007", "Revés", 9);
            Jugador j8 = new Jugador("Arturo", "Coello", "2008", "Drive", 9);

            for (Jugador j : Arrays.asList(j1, j2, j3, j4, j5, j6, j7, j8)) {
                gestor.registrarJugador(j);
            }

            // ---------------------------------------------------------
            // 5. CREAR EQUIPOS (4)
            // ---------------------------------------------------------
            System.out.println("-> Generando Equipos...");
            
            Equipo eq1 = new Equipo("Los Galacticos", Arrays.asList(j1, j2));
            Equipo eq2 = new Equipo("La Furia", Arrays.asList(j3, j4));
            Equipo eq3 = new Equipo("Leyendas", Arrays.asList(j5, j6));
            Equipo eq4 = new Equipo("Golden Boys", Arrays.asList(j7, j8));

            gestor.registrarEquipo(eq1);
            gestor.registrarEquipo(eq2);
            gestor.registrarEquipo(eq3);
            gestor.registrarEquipo(eq4);

            // ---------------------------------------------------------
            // 6. ESCENARIO A: TORNEO FINALIZADO ("Copa Apertura")
            // ---------------------------------------------------------
            System.out.println("-> Creando Torneo Finalizado...");
            
            Torneo torneoFin = new Torneo("Copa Apertura 2024", 4);
            gestor.registrarTorneo(torneoFin);

            // Inscribir equipos
            gestor.inscribirEquipoEnTorneo(torneoFin, eq1);
            gestor.inscribirEquipoEnTorneo(torneoFin, eq2);
            gestor.inscribirEquipoEnTorneo(torneoFin, eq3);
            gestor.inscribirEquipoEnTorneo(torneoFin, eq4);
            
            torneoFin.setEstado(Torneo.EstadoTorneo.EN_CURSO);

            // Semifinal 1 (Ganó Eq1)
            LocalDateTime fecha1 = LocalDateTime.now().minusDays(10).withHour(10).withMinute(0);
            Partido semi1 = new Partido(eq1, eq2, fecha1, 90, c1, a1, torneoFin);
            gestor.registrarPartidoSuelto(semi1);
            gestor.finalizarPartido(semi1, "6-4, 6-4", eq1);

            // Semifinal 2 (Ganó Eq4)
            LocalDateTime fecha2 = LocalDateTime.now().minusDays(10).withHour(12).withMinute(0);
            Partido semi2 = new Partido(eq3, eq4, fecha2, 90, c1, a2, torneoFin);
            gestor.registrarPartidoSuelto(semi2);
            gestor.finalizarPartido(semi2, "7-5, 6-2", eq4);

            // FINAL (Ganó Eq1)
            LocalDateTime fechaFinal = LocalDateTime.now().minusDays(8).withHour(18).withMinute(0);
            Partido finalMatch = new Partido(eq1, eq4, fechaFinal, 120, c3, a3, torneoFin);
            gestor.registrarPartidoSuelto(finalMatch);
            gestor.finalizarPartido(finalMatch, "6-3, 4-6, 7-5", eq1);

            // Finalizar el torneo
            gestor.finalizarTorneo(torneoFin, eq1);


            // ---------------------------------------------------------
            // 7. ESCENARIO B: TORNEO EN CURSO ("Liga de Invierno")
            // ---------------------------------------------------------
            System.out.println("-> Creando Torneo En Curso...");
            
            Torneo torneoCurso = new Torneo("Liga de Invierno 2025", 4);
            gestor.registrarTorneo(torneoCurso);
            
            // Inscribimos los mismos equipos (es válido en otra competición)
            gestor.inscribirEquipoEnTorneo(torneoCurso, eq1);
            gestor.inscribirEquipoEnTorneo(torneoCurso, eq2);
            gestor.inscribirEquipoEnTorneo(torneoCurso, eq3);
            gestor.inscribirEquipoEnTorneo(torneoCurso, eq4);
            
            torneoCurso.setEstado(Torneo.EstadoTorneo.EN_CURSO);

            // Partido Jugado (Ayer)
            LocalDateTime fechaCurso1 = LocalDateTime.now().minusDays(1).withHour(14).withMinute(0);
            Partido pCurso1 = new Partido(eq2, eq3, fechaCurso1, 90, c2, a1, torneoCurso);
            gestor.registrarPartidoSuelto(pCurso1);
            gestor.finalizarPartido(pCurso1, "6-0, 6-0", eq2);

            // Partido Pendiente (Mañana)
            LocalDateTime fechaCurso2 = LocalDateTime.now().plusDays(1).withHour(16).withMinute(0);
            // Verificar disponibilidad solo por seguridad
            if (gestor.validarDisponibilidadCancha(c4, fechaCurso2, 90)) {
                Partido pCurso2 = new Partido(eq1, eq4, fechaCurso2, 90, c4, a2, torneoCurso);
                gestor.registrarPartidoSuelto(pCurso2);
            }

            // ---------------------------------------------------------
            // 8. PARTIDOS SUELTOS (Amistosos)
            // ---------------------------------------------------------
            System.out.println("-> Creando Partidos Sueltos...");

            // Amistoso Finalizado (Hace 2 días)
            LocalDateTime fechaAmistoso1 = LocalDateTime.now().minusDays(2).withHour(20).withMinute(0);
            Partido amistoso1 = new Partido(eq1, eq3, fechaAmistoso1, 60, c2, a3, null);
            gestor.registrarPartidoSuelto(amistoso1);
            gestor.finalizarPartido(amistoso1, "6-3, 6-3", eq1);

            // Amistoso Pendiente (Hoy más tarde)
            LocalDateTime fechaAmistoso2 = LocalDateTime.now().plusHours(2).withMinute(0); // Dentro de 2 horas
            // Aseguramos que sea en horario válido (8-22)
            if (fechaAmistoso2.getHour() < 8) fechaAmistoso2 = fechaAmistoso2.withHour(8);
            if (fechaAmistoso2.getHour() > 20) fechaAmistoso2 = fechaAmistoso2.withHour(20);

            // Usamos cancha 1 de Sede Norte (c3)
            if (gestor.validarDisponibilidadCancha(c3, fechaAmistoso2, 90)) {
                Partido amistoso2 = new Partido(eq2, eq4, fechaAmistoso2, 90, c3, a1, null);
                gestor.registrarPartidoSuelto(amistoso2);
            }

            System.out.println("=== DATOS GENERADOS EXITOSAMENTE ===");
            System.out.println("-> Se han creado:");
            System.out.println("   - 2 Sedes, 4 Canchas, 3 Árbitros, 8 Jugadores, 4 Equipos.");
            System.out.println("   - 1 Torneo FINALIZADO con campeón.");
            System.out.println("   - 1 Torneo EN CURSO con partidos pendientes.");
            System.out.println("   - Partidos sueltos (amistosos) mixtos.");
            System.out.println("Archivo 'datos_padel.dat' listo. Ejecute 'Main.java'.");

        } catch (Exception e) {
            System.err.println("ERROR DURANTE LA GENERACIÓN DE DATOS:");
            e.printStackTrace();
        }
    }
}