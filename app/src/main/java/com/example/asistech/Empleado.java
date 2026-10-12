
package com.example.asistech;

public class Empleado {

    private int id;
    private String nombre;
    private String correo;
    private String area;
    private String cargo;
    private String fechaIngreso;
    private String estado;
    private String horaEntrada;
    private String horaSalida;
    private String asistencia;

    // Horas acumuladas para Reportes
    private int totalHoras;
    private int minutos;

    public Empleado(
            int id,
            String nombre,
            String correo,
            String area,
            String cargo,
            String fechaIngreso,
            String estado,
            String horaEntrada,
            String horaSalida,
            String asistencia) {

        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.area = area;
        this.cargo = cargo;
        this.fechaIngreso = fechaIngreso;
        this.estado = estado;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.asistencia = asistencia;

        this.totalHoras = 0;
        this.minutos = 0;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getArea() {
        return area;
    }

    public String getCargo() {
        return cargo;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public String getEstado() {
        return estado;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public String getAsistencia() {
        return asistencia;
    }

    public int getTotalHoras() {
        return totalHoras;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setTotalHoras(int totalHoras) {
        this.totalHoras = totalHoras;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }
}