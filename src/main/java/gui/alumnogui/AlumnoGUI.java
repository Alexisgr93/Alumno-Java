package gui.alumnogui;

import dao.AlumnoDaoSql; 
import dao.AlumnoDaoTxt; 
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
    private AlumnoDaoTxt daoTXT; 
    private AlumnoDaoSql daoSQL; 
    
    private String txtFilePath = ""; 

    public AlumnoGUI() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Gestión de Alumnos - TP");
        
        // Estado inicial de paneles
        dbConnPanel.setVisible(false);
        txtPanel.setVisible(true);
        verTodosCheckBox.setSelected(false); 
        
        // Inicialización del modelo con la lista base
        alumnosModel = new AlumnosModel(alumnos);
        alumnosTable.setModel(alumnosModel);
        
        // --- LISTENERS MANUALES ASIGNADOS ---
        verTodosCheckBox.addActionListener(evt -> refrescarGrilla());
        consutarButton.addActionListener(evt -> consultarButtonActionPerformed());
        jButton1.addActionListener(evt -> conectarBDButtonActionPerformed());
        
        // Enlaces para limpiar advertencias de métodos no usados
        // Nota: Si tus botones de la vista se llaman diferente (ej: crearButton), 
        // adaptá el nombre de la variable antes del punto.
        try {
            // Buscamos enlazar las acciones de ABM
            // Si NetBeans te da error porque las variables tienen otro nombre, revisá initComponents
        } catch (Exception e) {}

        // UBICACIÓN CORRECTA DEL WINDOW LISTENER (Adentro del constructor)
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
    }

    /**
     * Centraliza la recarga de datos desde el DAO filtrando activos/eliminados en la vista.
     */
    private void refrescarGrilla() {
        if (dao == null) return;
        try {
            List<Alumno> todosLosAlumnos = dao.findAll();
            List<Alumno> listaFiltrada = new ArrayList<>();
            
            boolean mostrarTodos = verTodosCheckBox.isSelected();
            
            for (Alumno alu : todosLosAlumnos) {
                if (mostrarTodos || "ACTIVO".equalsIgnoreCase(alu.getEstado())) {
                    listaFiltrada.add(alu);
                }
            }
            
            setAlumnosInModel(listaFiltrada);
        } catch (DAOException ex) {
            Logger.getLogger(AlumnoGUI.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error al cargar alumnos: " + ex.getMessage(), "Error de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Sincroniza la lista interna con la tabla y fuerza el redibujado visual.
     */
    private void setAlumnosInModel(final List<Alumno> nuevaLista) {
        this.alumnos.clear();
        this.alumnos.addAll(nuevaLista);
        alumnosModel.fireTableDataChanged(); 
    }

    /**
     * Alterna la vista de paneles y REUTILIZA las conexiones si ya existen.
     */
    private void repoComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        int seleccion = repoComboBox.getSelectedIndex();
        
        if (seleccion == 0) { // TXT
            txtPanel.setVisible(true);
            dbConnPanel.setVisible(false);
            if (daoTXT != null) {
                dao = daoTXT;
                refrescarGrilla();
            } else {
                setAlumnosInModel(new ArrayList<>()); 
            }
        } else { // Base de Datos
            txtPanel.setVisible(false);
            dbConnPanel.setVisible(true);
            if (daoSQL != null) {
                dao = daoSQL;
                refrescarGrilla();
            } else {
                setAlumnosInModel(new ArrayList<>()); 
            }
        }
    }

    /**
     * Acción para buscar el archivo TXT y establecer la conexión.
     */
    public void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String nuevoPath = chooser.getSelectedFile().getAbsolutePath();
            pathfileTextField.setText(nuevoPath);
            
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
                
                daoTXT = (AlumnoDaoTxt) DAOFactory.createDAO(config);
                dao = daoTXT;
                
                refrescarGrilla();
            } catch (DAOFactoryException | DAOException ex) {
                Logger.getLogger(AlumnoGUI.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Error de configuración: " + ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Conexión a SQL. REUTILIZA si ya está conectado.
     */
    private void conectarBDButtonActionPerformed() {
        String usuario = userDBTextField.getText().trim();
        if (usuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un usuario para la BD.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (daoSQL != null) {
            dao = daoSQL;
            refrescarGrilla();
            JOptionPane.showMessageDialog(this, "Reutilizando conexión existente.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            Map<String, String> config = new HashMap<>();
            config.put(TIPO_DAO, "SQL"); 
            config.put("USER", usuario);
            
            daoSQL = (AlumnoDaoSql) DAOFactory.createDAO(config); 
            dao = daoSQL;
            
            refrescarGrilla();
            JOptionPane.showMessageDialog(this, "Conexión SQL establecida con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (DAOFactoryException | DAOException ex) {
            Logger.getLogger(AlumnoGUI.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error al conectar a BD: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Acción ELIMINAR con Confirmación y Baja Lógica.
     */
    public void eliminarButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (dao == null) {
            JOptionPane.showMessageDialog(this, "No hay ningún repositorio conectado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int index = alumnosTable.getSelectedRow();
        if (index >= 0) {
            Alumno alu = alumnos.get(index);
            
            int resp = JOptionPane.showConfirmDialog(
                this, 
                "¿Está seguro de eliminar al alumno " + alu.getNombre() + " " + alu.getApellido() + "?", 
                "Confirmar Baja Lógica", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (resp == JOptionPane.YES_OPTION) {
                try {
                    dao.delete(alu.getDni()); 
                    refrescarGrilla(); 
                    JOptionPane.showMessageDialog(this, "Alumno eliminado correctamente (Baja Lógica).", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (DAOException ex) {
                    Logger.getLogger(AlumnoGUI.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this, "Error al eliminar en backend: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un alumno de la lista.", "Atención", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Acción CREAR: Abre el formulario de carga.
     */
    public void crearButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (dao == null) {
            JOptionPane.showMessageDialog(this, "Primero debe conectar un repositorio (TXT o BD).", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        FormularioAlumnoDialog dialog = new FormularioAlumnoDialog(this, true, null, dao, "CREAR");
        dialog.setVisible(true);
        refrescarGrilla(); 
    }

    /**
     * Acción MODIFICAR: Pasa el alumno seleccionado y bloquea la Clave Primaria (DNI).
     */
    public void modificarButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (dao == null) return;
        int index = alumnosTable.getSelectedRow();
        if (index >= 0) {
            Alumno alu = alumnos.get(index);
            FormularioAlumnoDialog dialog = new FormularioAlumnoDialog(this, true, alu, dao, "MODIFICAR");
            dialog.setVisible(true);
            refrescarGrilla();
        } else {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un alumno para modificar.", "Atención", "Atención", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Acción CONSULTAR: Modo sólo lectura para ver todos los campos.
     */
    private void consultarButtonActionPerformed() {
        int index = alumnosTable.getSelectedRow();
        if (index >= 0) {
            Alumno alu = alumnos.get(index);
            FormularioAlumnoDialog dialog = new FormularioAlumnoDialog(this, true, alu, dao, "CONSULTAR");
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un alumno para consultar.", "Atención", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Cierre limpio de los DAOs al cerrar la ventana.
     */
    public void formWindowClosing(java.awt.event.WindowEvent evt) {
        try {
            if (daoTXT != null) daoTXT.close();
            if (daoSQL != null) daoSQL.close();
        } catch (DAOException ex) {
            Logger.getLogger(AlumnoGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Inicializador de componentes mínimos requeridos de la interfaz Swing.
     */
    private void initComponents() {
        dbConnPanel = new javax.swing.JPanel();
        txtPanel = new javax.swing.JPanel();
        alumnosTable = new javax.swing.JTable();
        verTodosCheckBox = new javax.swing.JCheckBox();
        consutarButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton(); 
        repoComboBox = new javax.swing.JComboBox<>();
        pathfileTextField = new javax.swing.JTextField();
        userDBTextField = new javax.swing.JTextField();
        
        repoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TXT", "Base de Datos" }));
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        repoComboBox.addActionListener(evt -> repoComboBoxActionPerformed(evt));
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE)
        );
        pack();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new AlumnoGUI().setVisible(true));
    }
    
    // Componentes de interfaz gráfica declarados correctamente
    private javax.swing.JTable alumnosTable;
    private javax.swing.JButton consutarButton;
    private javax.swing.JPanel dbConnPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> repoComboBox;
    private javax.swing.JTextField pathfileTextField;
    private javax.swing.JPanel txtPanel;
    private javax.swing.JTextField userDBTextField;
    private javax.swing.JCheckBox verTodosCheckBox;
}