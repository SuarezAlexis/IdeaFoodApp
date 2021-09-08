package com.jainjo.ideafood.log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jainjo.ideafood.model.Idea;

public class IdeaViewModel extends ViewModel {
    private final MutableLiveData<Idea> idea = new MutableLiveData<>();

    public LiveData<Idea> getIdea() { return idea; }
    public void setIdea(Idea i) { idea.setValue(i); }
}
