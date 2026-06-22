package dao;

/**
 * Excepción personalizada para errores del backend (persistencia).
 */
public class DAOException extends Exception {
    public DAOException(String message) {
        super(message);
    }
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}