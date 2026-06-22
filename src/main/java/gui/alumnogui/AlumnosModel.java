package gui.alumnogui;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import persona.Alumno;

/**
 * Modelo de tabla personalizado para mostrar la lista de alumnos en la interfaz gráfica.
 */
public class AlumnosModel extends AbstractTableModel {

    private final List<Alumno> alumnos;
    private final String[] columnas = {"DNI", "Nombre", "Apellido", "Promedio"};

    // Constructor
    public AlumnosModel(List<Alumno> alumnos) {
        this.alumnos = alumnos;
    }

    @Override
    public int getRowCount() {
        return alumnos.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnas[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Alumno alumno = alumnos.get(rowIndex);
        
        // Estructura switch compatible con Java 8
        switch (columnIndex) {
            case 0:
                return alumno.getDni();
            case 1:
                return alumno.getNombre();
            case 2:
                return alumno.getApellido();
            case 3:
                return alumno.getPromedio();
            default:
                return null;
        }
    }
}