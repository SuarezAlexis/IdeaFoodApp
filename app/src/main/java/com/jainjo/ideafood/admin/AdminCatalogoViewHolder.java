package com.jainjo.ideafood.admin;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jainjo.ideafood.MainActivity;
import com.jainjo.ideafood.R;
import com.jainjo.ideafood.model.Catalogo;

public class AdminCatalogoViewHolder extends RecyclerView.ViewHolder {
    private CardView cardView;
    private TextView idTextView;
    private TextView nombreTextView;
    private TextView descripcionTextView;

    public AdminCatalogoViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.admin_catalogo_card_view);
        idTextView = (TextView) itemView.findViewById(R.id.catViewIdText);
        nombreTextView = (TextView) itemView.findViewById(R.id.catViewNombreText);
        descripcionTextView = (TextView) itemView.findViewById(R.id.catViewDescripcionText);
    }

    public void bindData(MainActivity mainActivity, final Catalogo cat) {
        idTextView.setText("" + cat.getId());
        nombreTextView.setText(cat.getNombre());
        descripcionTextView.setText(cat.getDescripcion());
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.updateFragment( AdminCatalogoFragment.newInstance(cat) );
            }
        });
    }
}

