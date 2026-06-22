package gui.alumnogui;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import persona.Alumno;

/**
 * Modelo de tabla optimizado para la grilla de Alumnos según los requerimientos del TP.
 * @author g.guzman
 */
public class AlumnosModel extends AbstractTableModel {
    
    private List<Alumno> alumnos;
    // Agregamos la columna de Estado requerida por las especificaciones
    private static final String[] ENCABEZADOS = {"DNI", "APELLIDO", "NOMBRE", "ESTADO"};

    public void setAlumnos(List<Alumno> alumnos) {
        this.alumnos = alumnos;
    }

    @Override
    public int getRowCount() {
        return alumnos != null ? alumnos.size() : 0;
    }

    @Override
    public int getColumnCount() {
        return ENCABEZADOS.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (alumnos == null || rowIndex >= alumnos.size()) {
            return null;
        }
        
        Alumno alu = alumnos.get(rowIndex);
        switch (columnIndex) {
            case 0 -> { return alu.getDni(); }
            case 1 -> { return alu.getApellido(); }
            case 2 -> { return alu.getNombre(); }
            case 3 -> { 
                // El estado suele ser un boolean (true=activo, false=eliminado) o un String/char.
                // Adaptalo según cómo esté definido en tu clase Alumno (ej: alu.getEstado() o alu.isActivo())
                return alu.getEstado(); 
            }
            default -> throw new AssertionError("Columna inválida");
        }
    }

    @Override
    public String getColumnName(int column) {
        return ENCABEZADOS[column];
    }
}
