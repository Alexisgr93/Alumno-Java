package dao;

import java.util.Map;
import persona.Alumno;

/**
 * Fábrica para instanciar los DAOs correspondientes (TXT o SQL).
 */
public class DAOFactory {
    public static final String TIPO_DAO = "TIPO_DAO";
    public static final String TIPO_DAO_TXT = "TXT";
    public static final String TIPO_DAO_SQL = "SQL";
    public static final String FULLPATH = "FULLPATH";

    public static DAO<Alumno, Integer> createDAO(Map<String, String> config) throws DAOFactoryException, DAOException {
        String tipo = config.get(TIPO_DAO);
        
        if (tipo == null) {
            throw new DAOFactoryException("No se especificó el tipo de DAO.");
        }
        
        if (tipo.equalsIgnoreCase(TIPO_DAO_TXT)) {
            String path = config.get(FULLPATH);
            if (path == null || path.isEmpty()) {
                throw new DAOFactoryException("Falta especificar la ruta del archivo TXT.");
            }
            return new AlumnoDaoTxt(path);
        } else if (tipo.equalsIgnoreCase(TIPO_DAO_SQL)) {
            String user = config.get("USER");
            return new AlumnoDaoSql(user);
        }
        
        throw new DAOFactoryException("Tipo de DAO no soportado: " + tipo);
    }
}