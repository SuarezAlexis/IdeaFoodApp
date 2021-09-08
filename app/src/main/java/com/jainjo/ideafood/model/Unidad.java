package com.jainjo.ideafood.model;

public class Unidad {
    private int id = -1;
    private String nombre;
    private String abr;
    private String magnitud;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getAbr() { return abr; }
    public void setAbr(String abr) { this.abr = abr; }

    public String getMagnitud() { return magnitud; }
    public void setMagnitud(String magnitud) { this.magnitud = magnitud; }
}
