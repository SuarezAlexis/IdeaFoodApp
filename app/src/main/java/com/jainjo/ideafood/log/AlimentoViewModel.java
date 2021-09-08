package com.jainjo.ideafood.log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jainjo.ideafood.model.Alimento;

public class AlimentoViewModel extends ViewModel {
    private final MutableLiveData<Alimento> alimento = new MutableLiveData<>();

    public LiveData<Alimento> getAlimento() { return alimento; }
    public void setAlimento(Alimento a) { alimento.setValue(a); }
}
