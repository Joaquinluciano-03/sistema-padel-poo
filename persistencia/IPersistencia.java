package persistencia;

import controlador.GestorSistema;

/**
 * Interfaz que define el contrato de persistencia para guardar y cargar el estado del sistema.
 * Esto desacopla el GestorSistema del método de almacenamiento concreto (serialización, DB, etc.).
 */
public interface IPersistencia {
    
    void guardarDatos(GestorSistema gestor);
    
    void cargarDatos(GestorSistema gestor);
}