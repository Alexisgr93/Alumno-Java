package persona;

import java.time.LocalDate;

public class Alumno {
    private int dni;
    private String nombre;
    private String apellido;
    private LocalDate fecNac;
    private double promedio;
    private int cantMatAprob;
    private LocalDate fecIng;
    private String estado; // "ACTIVO" o "ELIMINADO"

    // --- CONSTRUCTOR ---
    public Alumno() {
    }

    // --- GETTERS Y SETTERS ---
    public int getDni() { return dni; }
    public void setDni(int dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public LocalDate getFecNac() { return fecNac; }
    public void setFecNac(LocalDate fecNac) { this.fecNac = fecNac; }

    public double getPromedio() { return promedio; }
    public void setPromedio(double promedio) { this.promedio = promedio; }

    public int getCantMatAprob() { return cantMatAprob; }
    public void setCantMatAprob(int cantMatAprob) { this.cantMatAprob = cantMatAprob; }

    public LocalDate getFecIng() { return fecIng; }
    public void setFecIng(LocalDate fecIng) { this.fecIng = fecIng; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}