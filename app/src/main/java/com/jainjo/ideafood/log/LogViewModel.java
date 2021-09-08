package com.jainjo.ideafood.log;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.jainjo.ideafood.model.TipoIdea;

import java.util.Date;
import java.util.List;

public class LogViewModel extends ViewModel {
    private Date date;
    private LogDayFoodAdapter viewPagerAdapter;

    public LogViewModel(@NonNull final Date date, @NonNull final LogDayFoodAdapter viewPagerAdapter) {
        setDate(date);
        setViewPagerAdapter(viewPagerAdapter);
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull final Date date) {
        this.date = date;
    }

    public LogDayFoodAdapter getViewPagerAdapter() { return viewPagerAdapter; }

    public void setViewPagerAdapter(@NonNull final LogDayFoodAdapter viewPagerAdapter)
    { this.viewPagerAdapter = viewPagerAdapter; }
}
