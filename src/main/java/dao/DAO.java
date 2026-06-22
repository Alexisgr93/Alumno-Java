package dao;

import java.util.List;

/**
 * Interfaz genérica para el acceso a datos (Data Access Object).
 * @param <T> Tipo de entidad (ej: Alumno)
 * @param <K> Tipo de la clave primaria (ej: Integer para el DNI)
 */
public interface DAO<T, K> {
    void create(T entity) throws DAOException;
    void update(T entity) throws DAOException;
    void delete(K id) throws DAOException;
    List<T> findAll() throws DAOException;
}