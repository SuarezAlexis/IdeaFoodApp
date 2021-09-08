package com.jainjo.ideafood.model;

public abstract class Catalogo {
    private short id;
    private String nombre;
    private String descripcion;

    public Catalogo() { id = -1; }
    public Catalogo(short id, String nombre, String descripcion) {
        setId(id);
        setNombre(nombre);
        setDescripcion(descripcion);
    }

    public short getId() { return id; }
    public void setId(short id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
