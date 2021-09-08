package com.jainjo.ideafood.admin;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jainjo.ideafood.MainActivity;
import com.jainjo.ideafood.R;
import com.jainjo.ideafood.model.Producto;
import com.jainjo.ideafood.model.Unidad;
import com.jainjo.ideafood.util.AppUtils;
import com.jainjo.ideafood.util.DownloadImageTask;

public class AdminProductoViewHolder extends RecyclerView.ViewHolder {

    private CardView cardView;
    private TextView idTextView;
    private ImageView imageView;
    private TextView nombreTextView;
    private TextView descripcionTextView;
    private TextView tipoTextView;

    public AdminProductoViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.admin_producto_card_view);
        idTextView = itemView.findViewById(R.id.productoViewIdText);
        imageView = itemView.findViewById(R.id.productoViewImageView);
        nombreTextView = itemView.findViewById(R.id.productoViewNombreText);
        descripcionTextView = itemView.findViewById(R.id.productoViewDescripcionText);
        tipoTextView = itemView.findViewById(R.id.productoViewTipoText);
    }

    public void bindData(MainActivity mainActivity, Producto producto) {
        idTextView.setText("" + producto.getId());
        nombreTextView.setText(producto.getNombre());
        descripcionTextView.setText(producto.getDescripcion());
        tipoTextView.setText(producto.getTipo().getNombre());
        new DownloadImageTask(imageView).execute(producto.getImagen());
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.updateFragment( AdminProductoFragment.newInstance(producto) );
            }
        });
    }
}
