package com.jainjo.ideafood.admin;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jainjo.ideafood.MainActivity;
import com.jainjo.ideafood.R;
import com.jainjo.ideafood.model.Unidad;

public class AdminUnidadViewHolder extends RecyclerView.ViewHolder {
    private CardView cardView;
    private TextView idTextView;
    private TextView nombreTextView;
    private TextView abrTextView;
    private TextView magnitudTextView;

    public AdminUnidadViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.admin_unidad_card_view);
        idTextView = itemView.findViewById(R.id.unidadViewIdText);
        nombreTextView = itemView.findViewById(R.id.unidadViewNombreText);
        abrTextView = itemView.findViewById(R.id.unidadViewAbrText);
        magnitudTextView = itemView.findViewById(R.id.unidadViewMagnitudText);
    }

    public void bindData(MainActivity mainActivity, Unidad unidad) {
        idTextView.setText("" + unidad.getId());
        nombreTextView.setText(unidad.getNombre());
        abrTextView.setText(unidad.getAbr());
        magnitudTextView.setText(unidad.getMagnitud());
        cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mainActivity.updateFragment( AdminUnidadFragment.newInstance(unidad) );
            }
        });
    }

}
