package com.jainjo.ideafood.model;

public class Producto {
    private long id = -1;
    private String nombre;
    private String descripcion;
    private String imagen;
    private TipoProducto tipo;

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
        if( imagen != "null" )this.imagen = imagen;
    }

    public TipoProducto getTipo() { return tipo; }
    public void setTipo(TipoProducto tipo) { this.tipo = tipo; }

}


