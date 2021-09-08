package com.jainjo.ideafood.log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.jainjo.ideafood.model.TipoIdea;

import java.util.ArrayList;
import java.util.List;

public class LogDayFoodAdapter extends FragmentStateAdapter {

    private List<TipoIdea> tiposIdea;

    public LogDayFoodAdapter(@NonNull Fragment fragment, List<TipoIdea> tiposIdea) {
        super(fragment);
        this.tiposIdea = tiposIdea;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        LogDayTipoIdeaFragment fragment = LogDayTipoIdeaFragment.newInstance(tiposIdea.get(position).getId(), tiposIdea.get(position).getNombre());
        return fragment;
    }

    @Override
    public int getItemCount() {
        return tiposIdea.size();
    }

    @Override
    public long getItemId(int position) {
        if(position <= getItemCount()) { return tiposIdea.get(position).getId(); }
        return -1;
    }

    public List<TipoIdea> getTiposIdea() { return tiposIdea; }
    public void setTiposIdea(List<TipoIdea> tiposIdea) { this.tiposIdea = tiposIdea; }
}
