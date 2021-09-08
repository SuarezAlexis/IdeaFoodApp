package com.jainjo.ideafood.model;

public class TipoProducto extends Catalogo {

    public TipoProducto() { super(); }

    public TipoProducto(short id, String nombre, String descripcion)
    { super(id,nombre,descripcion); }

    @Override
    public String toString() { return this.getNombre(); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final TipoProducto other = (TipoProducto) obj;
        return other.getId() == this.getId();
    }

}
