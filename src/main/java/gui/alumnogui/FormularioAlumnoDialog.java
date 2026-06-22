package gui.alumnogui;

import dao.DAO;
import dao.DAOException;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import persona.Alumno;

/**
 * Formulario dinámico secundario para resolver las acciones de Crear, Modificar y Consultar.
 * Resuelve las validaciones, captura excepciones y muestra popups según el TP.
 */
public class FormularioAlumnoDialog extends javax.swing.JDialog {
    
    private final DAO<Alumno, Integer> dao;
    private final Alumno alumnoExistente;
    private final String modo; // "CREAR", "MODIFICAR" o "CONSULTAR"

    // Componentes visuales dinámicos
    private JTextField txtDni, txtNombre, txtApellido, txtFecNac, txtPromedio, txtCantMatAprob, txtFecIng, txtEstado;
    private JButton btnGuardar, btnCancelar;

    public FormularioAlumnoDialog(java.awt.Frame parent, boolean modal, Alumno alumno, DAO<Alumno, Integer> dao, String modo) {
        super(parent, modal);
        this.alumnoExistente = alumno;
        this.dao = dao;
        this.modo = modo;
        
        inicializarComponentes();
        cargarDatosSiCorresponde();
        ajustarModoUI();
        
        setTitle(modo + " Alumno");
        pack();
        setLocationRelativeTo(parent);
    }

    private void inicializarComponentes() {
        // Armamos un layout estructurado para los 8 campos mencionados en la aclaración
        setLayout(new GridLayout(10, 2, 10, 10));
        
        add(new JLabel(" DNI (Clave Primaria):")); txtDni = new JTextField(); add(txtDni);
        add(new JLabel(" Apellido:")); txtApellido = new JTextField(); add(txtApellido);
        add(new JLabel(" Nombre:")); txtNombre = new JTextField(); add(txtNombre);
        add(new JLabel(" Fecha Nacimiento (AAAA-MM-DD):")); txtFecNac = new JTextField(); add(txtFecNac);
        add(new JLabel(" Promedio:")); txtPromedio = new JTextField(); add(txtPromedio);
        add(new JLabel(" Cant. Materias Aprobadas:")); txtCantMatAprob = new JTextField(); add(txtCantMatAprob);
        add(new JLabel(" Fecha Ingreso (AAAA-MM-DD):")); txtFecIng = new JTextField(); add(txtFecIng);
        add(new JLabel(" Estado:")); txtEstado = new JTextField("ACTIVO"); add(txtEstado); // Por defecto activo

        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        add(btnGuardar);
        add(btnCancelar);

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> ejecutarAccionGuardar());
    }

    private void cargarDatosSiCorresponde() {
        if (alumnoExistente != null) {
            txtDni.setText(String.valueOf(alumnoExistente.getDni()));
            txtApellido.setText(alumnoExistente.getApellido());
            txtNombre.setText(alumnoExistente.getNombre());
            txtFecNac.setText(alumnoExistente.getFecNac() != null ? alumnoExistente.getFecNac().toString() : "");
            txtPromedio.setText(String.valueOf(alumnoExistente.getPromedio()));
            txtCantMatAprob.setText(String.valueOf(alumnoExistente.getCantMatAprob()));
            txtFecIng.setText(alumnoExistente.getFecIng() != null ? alumnoExistente.getFecIng().toString() : "");
            txtEstado.setText(String.valueOf(alumnoExistente.getEstado()));
        }
    }

    private void ajustarModoUI() {
        if ("MODIFICAR".equals(modo)) {
            txtDni.setEditable(false); // ✏️ Requerimiento: No se debe poder modificar el DNI (PK)
        } else if ("CONSULTAR".equals(modo)) {
            // 📋 Requerimiento: Sólo lectura para visualizar datos completos
            txtDni.setEditable(false);
            txtApellido.setEditable(false);
            txtNombre.setEditable(false);
            txtFecNac.setEditable(false);
            txtPromedio.setEditable(false);
            txtCantMatAprob.setEditable(false);
            txtFecIng.setEditable(false);
            txtEstado.setEditable(false);
            btnGuardar.setVisible(false); // No se necesita guardar en consulta
        }
    }

    private void ejecutarAccionGuardar() {
        try {
            // 1. Validaciones de obligatoriedad en Interfaz Gráfica
            if (txtDni.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Los campos DNI, Apellido y Nombre son estrictamente obligatorios.");
            }

            // Parseos básicos de tipos de datos
            int dni = Integer.parseInt(txtDni.getText().trim());
            String apellido = txtApellido.getText().trim();
            String nombre = txtNombre.getText().trim();
            LocalDate fecNac = LocalDate.parse(txtFecNac.getText().trim());
            double promedio = Double.parseDouble(txtPromedio.getText().trim());
            int cantMat = Integer.parseInt(txtCantMatAprob.getText().trim());
            LocalDate fecIng = LocalDate.parse(txtFecIng.getText().trim());
            
            // Asumiendo el tipo de dato que maneje tu backend para estado (ej. String o boolean)
            String estado = txtEstado.getText().trim(); 

            // 2. Armado o actualización del objeto de dominio
            Alumno alu = (alumnoExistente != null) ? alumnoExistente : new Alumno();
            alu.setDni(dni);
            alu.setApellido(apellido);
            alu.setNombre(nombre);
            alu.setFecNac(fecNac);
            alu.setPromedio(promedio);
            alu.setCantMatAprob(cantMat);
            alu.setFecIng(fecIng);
            alu.setEstado(estado);

            // 3. Persistencia mediante DAO según modo
            if ("CREAR".equals(modo)) {
                dao.create(alu);
                JOptionPane.showMessageDialog(this, "Alumno creado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else if ("MODIFICAR".equals(modo)) {
                dao.update(alu);
                JOptionPane.showMessageDialog(this, "Alumno modificado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

            dispose(); // Cerramos formulario tras el éxito

        } catch (IllegalArgumentException | DateTimeParseException ex) {
            // ⚠️ Manejo de Errores del Frontend (ej. formato fecha incorrecto o nulos)
            JOptionPane.showMessageDialog(this, "Error de validación: " + ex.getMessage(), "Campos Inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (DAOException ex) {
            // ⚠️ Manejo de Errores lanzados por excepciones desde el Backend
            JOptionPane.showMessageDialog(this, "Error en Base de Datos/Archivo: " + ex.getMessage(), "Error de Persistencia", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}