package com.jainjo.ideafood.admin;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jainjo.ideafood.AdminAlimentoFragment;
import com.jainjo.ideafood.MainActivity;
import com.jainjo.ideafood.R;
import com.jainjo.ideafood.model.Alimento;
import com.jainjo.ideafood.util.DownloadImageTask;

public class AdminAlimentoViewHolder extends RecyclerView.ViewHolder {
    private CardView cardView;
    private TextView idTextView;
    private ImageView imageView;
    private TextView nombreTextView;
    private TextView descripcionTextView;
    private TextView tipoTextView;

    public AdminAlimentoViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.admin_alimento_card_view);
        idTextView = itemView.findViewById(R.id.alimentoViewIdText);
        imageView = itemView.findViewById(R.id.alimentoViewImageView);
        nombreTextView = itemView.findViewById(R.id.alimentoViewNombreText);
        descripcionTextView = itemView.findViewById(R.id.alimentoViewDescripcionText);
        tipoTextView = itemView.findViewById(R.id.alimentoViewTipoText);
    }

    public void bindData(MainActivity mainActivity, Alimento alimento) {
        idTextView.setText("" + alimento.getId());
        nombreTextView.setText(alimento.getNombre());
        descripcionTextView.setText(alimento.getDescripcion());
        tipoTextView.setText(alimento.getTipo().getNombre());
        new DownloadImageTask(imageView).execute(alimento.getImagen());
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.updateFragment( AdminAlimentoFragment.newInstance(alimento) );
            }
        });
    }
}
