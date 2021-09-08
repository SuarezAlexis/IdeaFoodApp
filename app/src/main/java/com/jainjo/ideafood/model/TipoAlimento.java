package com.jainjo.ideafood.model;

public class TipoAlimento extends Catalogo {

    public TipoAlimento() { super(); }

    public TipoAlimento(short id, String nombre, String descripcion)
    { super(id,nombre, descripcion); }

    @Override
    public String toString() {
        return getNombre();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final TipoAlimento other = (TipoAlimento) obj;
        return other.getId() == this.getId() && this.getId() != -1;
    }
}
