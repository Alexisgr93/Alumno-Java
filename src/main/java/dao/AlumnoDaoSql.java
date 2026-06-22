package dao;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import persona.Alumno;

public class AlumnoDaoSql implements DAO<Alumno, Integer> {
    
    private final String usuario;
    private List<Alumno> mockMemoryDB = new ArrayList<>();

    // CONSTRUCTOR ÚNICO - Envuelve correctamente las asignaciones de prueba
    public AlumnoDaoSql(String usuario) {
        this.usuario = usuario;
        
        // --- ALUMNOS DE PRUEBA ---
        Alumno alu1 = new Alumno();
        alu1.setDni(12345678);
        alu1.setNombre("Juan");
        alu1.setApellido("Pérez");
        alu1.setFecNac(LocalDate.of(2000, 5, 15));
        alu1.setFecIng(LocalDate.now());
        alu1.setEstado("ACTIVO");
        mockMemoryDB.add(alu1);

        Alumno alu2 = new Alumno();
        alu2.setDni(87654321);
        alu2.setNombre("Ana");
        alu2.setApellido("Gómez");
        alu2.setFecNac(LocalDate.of(1999, 8, 22));
        alu2.setFecIng(LocalDate.now());
        alu2.setEstado("ELIMINADO");
        mockMemoryDB.add(alu2);
    }

    @Override
    public void create(Alumno entity) throws DAOException {
        mockMemoryDB.add(entity);
    }

    @Override
    public void update(Alumno entity) throws DAOException {
        for (int i = 0; i < mockMemoryDB.size(); i++) {
            if (mockMemoryDB.get(i).getDni() == entity.getDni()) {
                mockMemoryDB.set(i, entity);
                return;
            }
        }
    }

    @Override
    public void delete(Integer id) throws DAOException {
        for (Alumno a : mockMemoryDB) {
            if (a.getDni() == id) {
                a.setEstado("ELIMINADO");
                return;
            }
        }
    }

    @Override
    public List<Alumno> findAll() throws DAOException {
        return mockMemoryDB;
    }
}