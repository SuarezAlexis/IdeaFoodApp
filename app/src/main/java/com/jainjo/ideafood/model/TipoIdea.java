package com.jainjo.ideafood.model;

public class TipoIdea extends  Catalogo {

    public TipoIdea() { super(); }

    public TipoIdea(short id, String nombre, String descripcion)
    { super(id,nombre,descripcion); }

    @Override
    public String toString() {
        return getNombre();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final TipoIdea other = (TipoIdea) obj;
        return other.getId() == this.getId() && this.getId() != -1;
    }
}
