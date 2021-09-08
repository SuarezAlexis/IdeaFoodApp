package com.jainjo.ideafood.log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jainjo.ideafood.MainActivity;
import com.jainjo.ideafood.R;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter {
    private List<LogViewModel> models;
    private MainActivity activity;

    public LogAdapter( List<LogViewModel> models, MainActivity activity )
    {
        this.models = models;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_day_view, parent, false);
        RecyclerView.ViewHolder vh = new LogDayViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((LogDayViewHolder)holder).bindData( models.get(position), activity );
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
