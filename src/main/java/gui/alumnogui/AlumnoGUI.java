package gui.alumnogui;

import dao.AlumnoDAOSQL;
import dao.AlumnoDAOTXT;
import dao.DAO;
import dao.DAOException;
import dao.DAOFactory;
import static dao.DAOFactory.FULLPATH;
import static dao.DAOFactory.TIPO_DAO;
import dao.DAOFactoryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import persona.Alumno;

/**
 * Controlador de la interfaz principal remodelado para cumplir las especificaciones del TP.
 * @author g.guzman
 */
public class AlumnoGUI extends javax.swing.JFrame {

    private List<Alumno> alumnos = new ArrayList<>();
    private AlumnosModel alumnosModel;
    
    private DAO<Alumno, Integer> dao;
    private AlumnoDAOTXT daoTXT;
    private AlumnoDAOSQL daoSQL;
    
    private String txtFilePath = ""; // Guardamos el path para reutilizar conexión TXT

    public AlumnoGUI() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Gestión de Alumnos - TP");
        
        // Estado inicial de paneles
        dbConnPanel.setVisible(false);
        txtPanel.setVisible(true);
        verTodosCheckBox.setSelected(false); // Por defecto desmarcado (No ver eliminados)
        
        alumnosModel = new AlumnosModel();
        alumnosModel.setAlumnos(alumnos);
        alumnosTable.setModel(alumnosModel);
        
        // Listener manual para el CheckBox de eliminados
        verTodosCheckBox.addActionListener(evt -> refrescarGrilla());
        
        // Listener para el botón Consultar (que faltaba asignar en el .form)
        consutarButton.addActionListener(evt -> consultarButtonActionPerformed());
        
        // Listener para conectar a Base de Datos
        jButton1.addActionListener(evt -> conectarBDButtonActionPerformed());
    }

    // [initComponents() omitido por espacio, se mantiene igual al generado por NetBeans]

    /**
     * Centraliza la recarga de datos desde el DAO capturando excepciones mediante popups.
     */
    private void refrescarGrilla() {
        if (dao == null) return;
        try {
            // Pasamos el estado del checkbox: true para incluir eliminados, false para solo activos
            boolean incluirEliminados = verTodosCheckBox.isSelected();
            List<Alumno> listaActualizada = dao.findAll(incluirEliminados);
            setAlumnosInModel(listaActualizada);
        } catch (DAOException ex) {
            Logger.getLogger(AlumnoGUI.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error al cargar alumnos: " + ex.getMessage(), "Error de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setAlumnosInModel(final List<Alumno> alumnos1) {
        this.alumnos = alumnos1;
        alumnosModel.setAlumnos(alumnos1);
        alumnosModel.fireTableDataChanged();
    }

    /**
     * Alterna la vista de paneles y REUTILIZA las conexiones si ya existen.
     */
    private void repoComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_repoComboBoxActionPerformed
        int seleccion = repoComboBox.getSelectedIndex();
        
        if (seleccion == 0) { // TXT
            txtPanel.setVisible(true);
            dbConnPanel.setVisible(false);
            if (daoTXT != null) {
                dao = daoTXT;
                refrescarGrilla();
            } else {
                setAlumnosInModel(new ArrayList<>()); // Limpiar grilla hasta que elija archivo
            }
        } else { // Base de Datos
            txtPanel.setVisible(false);
            dbConnPanel.setVisible(true);
            if (daoSQL != null) {
                dao = daoSQL;
                refrescarGrilla();
            } else {
                setAlumnosInModel(new ArrayList<>()); // Limpiar grilla hasta que se conecte
            }
        }
    }//GEN-LAST:event_repoComboBoxActionPerformed

    /**
     * Acción para buscar el archivo TXT y establecer la conexión.
     */
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String nuevoPath = chooser.getSelectedFile().getAbsolutePath();
            pathfileTextField.setText(nuevoPath);
            
            // 🔁 REUTILIZACIÓN: Si el archivo es el mismo y ya existe el DAO, no lo recreamos
            if (daoTXT != null && nuevoPath.equals(txtFilePath)) {
                dao = daoTXT;
                refrescarGrilla();
                return;
            }
            
            try {
                txtFilePath = nuevoPath;
                Map<String, String> config = new HashMap<>();
                config.put(TIPO_DAO, DAOFactory.TIPO_DAO_TXT);
                config.put(FULLPATH, txtFilePath);
                
                daoTXT = (AlumnoDAOTXT) DAOFactory.createDAO(config);
                dao = daoTXT;
                
                refrescarGrilla();
            } catch (DAOFactoryException | DAOException ex) {
                Logger.getLogger(AlumnoGUI.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Error de configuración: " + ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    /**
     * Conexión a SQL (Lógica asociada a tu jButton1). REUTILIZA si ya está conectado.
     */
    private void conectarBDButtonActionPerformed() {
        String usuario = userDBTextField.getText().trim();
        if (usuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un usuario para la BD.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 🔁 REUTILIZACIÓN: Si ya existe la instancia SQL, evitamos volver a conectarnos
        if (daoSQL != null) {
            dao = daoSQL;
            refrescarGrilla();
            JOptionPane.showMessageDialog(this, "Reutilizando conexión existente.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            Map<String, String> config = new HashMap<>();
            config.put(TIPO_DAO, "SQL"); // O la constante equivalente en tu DAOFactory
            config.put("USER", usuario);
            // Agrega acá los demás parámetros requeridos por tu DAOFactory para SQL (url, password, etc.)
            
            // Suponiendo que tu factory devuelve AlumnoDAOSQL para este tipo:
            daoSQL = (AlumnoDAOSQL) DAOFactory.createDAO(config); 
            dao = daoSQL;
            
            refrescarGrilla();
            JOptionPane.showMessageDialog(this, "Conexión SQL establecida con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (DAOFactoryException | DAOException ex) {
            Logger.getLogger(AlumnoGUI.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error al conectar a BD: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * ❌ Acción ELIMINAR con Confirmación y Baja Lógica.
     */
    private void eliminarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarButtonActionPerformed
        if (dao == null) {
            JOptionPane.showMessageDialog(this, "No hay ningún repositorio conectado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int index = alumnosTable.getSelectedRow();
        if (index >= 0) {
            Alumno alu = alumnos.get(index);
            
            // Mensaje de confirmación obligatorio
            int resp = JOptionPane.showConfirmDialog(
                this, 
                "¿Está seguro de eliminar al alumno " + alu.getNombre() + " " + alu.getApellido() + "?", 
                "Confirmar Baja Lógica", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (resp == JOptionPane.YES_OPTION) {
                try {
                    // El DAO se encarga de cambiar el atributo 'estado' a inactivo internamente (Baja Lógica)
                    dao.delete(alu.getDni()); 
                    refrescarGrilla(); // Recargamos para reflejar el cambio inmediatamente
                    JOptionPane.showMessageDialog(this, "Alumno eliminado correctamente (Baja Lógica).", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (DAOException ex) {
                    Logger.getLogger(AlumnoGUI.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this, "Error al eliminar en backend: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un alumno de la lista.", "Atención", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_eliminarButtonActionPerformed

    /**
     * ✏️ Acción CREAR: Abre el formulario de carga.
     */
    private void crearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_crearButtonActionPerformed
        if (dao == null) {
            JOptionPane.showMessageDialog(this, "Primero debe conectar un repositorio (TXT o BD).", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Pasamos null en 'alumno' porque es una creación de registro nuevo
        FormularioAlumnoDialog dialog = new FormularioAlumnoDialog(this, true, null, dao, "CREAR");
        dialog.setVisible(true);
        refrescarGrilla(); // Al cerrar el diálogo, refresca los cambios
    }//GEN-LAST:event_crearButtonActionPerformed

    /**
     * ✏️ Acción MODIFICAR: Pasa el alumno seleccionado y bloquea la Clave Primaria (DNI).
     */
    private void modificarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modificarButtonActionPerformed
        if (dao == null) return;
        int index = alumnosTable.getSelectedRow();
        if (index >= 0) {
            Alumno alu = alumnos.get(index);
            // Modo MODIFICAR
            FormularioAlumnoDialog dialog = new FormularioAlumnoDialog(this, true, alu, dao, "MODIFICAR");
            dialog.setVisible(true);
            refrescarGrilla();
        } else {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un alumno para modificar.", "Atención", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_modificarButtonActionPerformed

    /**
     * 📋 Acción CONSULTAR: Modo sólo lectura para ver todos los campos.
     */
    private void consultarButtonActionPerformed() {
        int index = alumnosTable.getSelectedRow();
        if (index >= 0) {
            Alumno alu = alumnos.get(index);
            // Modo CONSULTAR
            FormularioAlumnoDialog dialog = new FormularioAlumnoDialog(this, true, alu, dao, "CONSULTAR");
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un alumno para consultar.", "Atención", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            if (daoTXT != null) daoTXT.close();
            if (daoSQL != null) daoSQL.close();
        } catch (DAOException ex) {
            Logger.getLogger(AlumnoGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowClosing

    public static void main(String args[]) {
        // [Cuerpo del Main por defecto de NetBeans para levantar la View]
        java.awt.EventQueue.invokeLater(() -> new AlumnoGUI().setVisible(true));
    }
    
    // ... Variables declaration automática ...
}
