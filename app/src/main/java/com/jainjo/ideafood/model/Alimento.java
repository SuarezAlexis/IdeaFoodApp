package com.jainjo.ideafood.model;

public class Alimento {
    private long id = -1;
    private String nombre;
    private String descripcion;
    private String imagen;
    private TipoAlimento tipo;
    private String username;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) {
        if( descripcion != "null" ) this.descripcion = descripcion;
    }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) {
        if( imagen != "null" ) this.imagen = imagen;
    }

    public TipoAlimento getTipo() { return tipo; }
    public void setTipo(TipoAlimento tipo) { this.tipo = tipo; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public String toString() { return getNombre(); }
}
