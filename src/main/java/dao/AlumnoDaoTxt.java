package dao;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import persona.Alumno;

/**
 * Persistencia en archivos de texto (TXT) con soporte de Baja Lógica.
 */
public class AlumnoDaoTxt implements DAO<Alumno, Integer> {
    
    private final String filePath;

    public AlumnoDaoTxt(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void create(Alumno alu) throws DAOException {
        List<Alumno> lista = findAll();
        // Validar si el DNI ya existe
        for (Alumno a : lista) {
            if (a.getDni() == alu.getDni()) {
                throw new DAOException("El alumno con DNI " + alu.getDni() + " ya existe.");
            }
        }
        lista.add(alu);
        guardarTodo(lista);
    }

    @Override
    public void update(Alumno alu) throws DAOException {
        List<Alumno> lista = findAll();
        boolean encontrado = false;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getDni() == alu.getDni()) {
                lista.set(i, alu); // Reemplaza con los nuevos datos
                encontrado = true;
                break;
            }
        }
        if (!encontrado) throw new DAOException("Alumno no encontrado para modificar.");
        guardarTodo(lista);
    }

    @Override
    public void delete(Integer dni) throws DAOException {
        List<Alumno> lista = findAll();
        boolean encontrado = false;
        for (Alumno a : lista) {
            if (a.getDni() == dni) {
                // ❌ BAJA LÓGICA: En vez de removerlo de la lista, cambiamos su estado
                a.setEstado("ELIMINADO"); 
                encontrado = true;
                break;
            }
        }
        if (!encontrado) throw new DAOException("No se encontró el alumno con DNI: " + dni);
        guardarTodo(lista);
    }

    @Override
    public List<Alumno> findAll() throws DAOException {
        List<Alumno> lista = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return lista; // Si el archivo no existe, devuelve lista vacía

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                // Separamos los datos por coma (DNI,Nombre,Apellido,FecNac,Promedio,Materias,FecIng,Estado)
                String[] datos = linea.split(",");
                if (datos.length >= 8) {
                    Alumno alu = new Alumno();
                    alu.setDni(Integer.parseInt(datos[0]));
                    alu.setNombre(datos[1]);
                    alu.setApellido(datos[2]);
                    alu.setFecNac(LocalDate.parse(datos[3]));
                    alu.setPromedio(Double.parseDouble(datos[4]));
                    alu.setCantMatAprob(Integer.parseInt(datos[5]));
                    alu.setFecIng(LocalDate.parse(datos[6]));
                    alu.setEstado(datos[7]);
                    lista.add(alu);
                }
            }
        } catch (Exception e) {
            throw new DAOException("Error al leer el archivo de texto: " + e.getMessage(), e);
        }
        return lista;
    }

    private void guardarTodo(List<Alumno> lista) throws DAOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (Alumno a : lista) {
                pw.println(a.getDni() + "," +
                           a.getNombre() + "," +
                           a.getApellido() + "," +
                           a.getFecNac() + "," +
                           a.getPromedio() + "," +
                           a.getCantMatAprob() + "," +
                           a.getFecIng() + "," +
                           a.getEstado());
            }
        } catch (IOException e) {
            throw new DAOException("Error al escribir en el archivo de texto.", e);
        }
    }

    public void close() throws DAOException {
        // Método de cierre opcional requerido por la GUI
    }
}