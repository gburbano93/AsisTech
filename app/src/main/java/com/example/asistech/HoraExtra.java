
package com.example.asistech;

public class HoraExtra {

    private int id;
    private int usuarioId;
    private String nombre;
    private String area;
    private String cargo;
    private String fecha;
    private double horas;
    private String motivo;
    private String estado;

    public HoraExtra(
            int id,
            int usuarioId,
            String nombre,
            String area,
            String cargo,
            String fecha,
            double horas,
            String motivo,
            String estado) {

        this.id = id;
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.area = area;
        this.cargo = cargo;
        this.fecha = fecha;
        this.horas = horas;
        this.motivo = motivo;
        this.estado = estado;
    }

    public int getId() { return id; }
    public int getUsuarioId() { return usuarioId; }
    public String getNombre() { return nombre; }
    public String getArea() { return area; }
    public String getCargo() { return cargo; }
    public String getFecha() { return fecha; }
    public double getHoras() { return horas; }
    public String getMotivo() { return motivo; }
    public String getEstado() { return estado; }
}
