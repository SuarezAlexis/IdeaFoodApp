package com.jainjo.ideafood.model;

import java.util.ArrayList;
import java.util.List;

public class Idea {
    private long id = -1;
    private String nombre;
    private String descripcion;
    private String imagen;
    private TipoIdea tipo;
    private String usuario;
    private List<Alimento> alimentoList = new ArrayList<>();

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public TipoIdea getTipo() { return tipo; }
    public void setTipo(TipoIdea tipo) { this.tipo = tipo; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public List<Alimento> getAlimentos() { return alimentoList; }
    public void setUsuario(List<Alimento> alimentos) { this.alimentoList = alimentos; }
}
