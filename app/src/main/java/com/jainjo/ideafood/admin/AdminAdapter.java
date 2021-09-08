package com.jainjo.ideafood.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jainjo.ideafood.MainActivity;
import com.jainjo.ideafood.R;
import com.jainjo.ideafood.log.LogDayViewHolder;
import com.jainjo.ideafood.model.Alimento;
import com.jainjo.ideafood.model.Catalogo;
import com.jainjo.ideafood.model.Producto;
import com.jainjo.ideafood.model.Unidad;

import java.util.List;

public class AdminAdapter extends RecyclerView.Adapter {

    private MainActivity mainActivity;
    private List<Object> models;
    private ManageableEntity entity;

    public AdminAdapter(MainActivity mainActivity, List<Object> models, ManageableEntity entity) {
        this.mainActivity = mainActivity;
        this.models = models;
        this.entity = entity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        View v;
        switch(entity) {
            case TIPO_ALIMENTO:
            case TIPO_IDEA:
            case TIPO_PRODUCTO:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_catalogo_view, parent, false);
                vh = new AdminCatalogoViewHolder(v);
                break;
            case UNIDAD:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_unidad_view, parent, false);
                vh = new AdminUnidadViewHolder(v);
                break;
            case PRODUCTO:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_producto_view, parent, false);
                vh = new AdminProductoViewHolder(v);
                break;
            case ALIMENTO:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_alimento_view, parent, false);
                vh = new AdminAlimentoViewHolder(v);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch(entity) {
            case TIPO_ALIMENTO:
            case TIPO_IDEA:
            case TIPO_PRODUCTO:
                ((AdminCatalogoViewHolder)holder).bindData(mainActivity, (Catalogo)models.get(position) );
                break;
            case UNIDAD:
                ((AdminUnidadViewHolder)holder).bindData(mainActivity, (Unidad)models.get(position) );
                break;
            case PRODUCTO:
                ((AdminProductoViewHolder)holder).bindData(mainActivity, (Producto)models.get(position) );
                break;
            case ALIMENTO:
                ((AdminAlimentoViewHolder)holder).bindData(mainActivity, (Alimento)models.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() { return models.size(); }
}
